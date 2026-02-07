package org.post_hub.repository.impl;

import org.post_hub.config.DatabaseUtil;
import org.post_hub.model.Label;
import org.post_hub.model.Post;
import org.post_hub.model.PostStatus;
import org.post_hub.repository.PostRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcPostRepositoryImpl implements PostRepository {
    private final Connection connection;

    public JdbcPostRepositoryImpl() {
        this.connection = DatabaseUtil.getConnection();
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to configure DB connection (autoCommit=false)", e);
        }
    }

    @Override
    public Post save(Post post) {
        String insertPostSql = "INSERT INTO posts (content, created, updated, status, writer_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertPostSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getContent());
            statement.setTimestamp(2, Timestamp.valueOf(post.getCreated()));
            statement.setTimestamp(3, Timestamp.valueOf(post.getUpdated()));
            statement.setString(4, post.getStatus().toString());

            if (post.getWriterId() != null) {
                statement.setLong(5, post.getWriterId());
            } else {
                statement.setNull(5, Types.BIGINT);
            }

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getLong(1));
                }
            }

            savePostLabels(post);
            connection.commit();

        } catch (SQLException e) {
            rollbackQuietly();
            throw new RuntimeException("Failed to save post", e);
        }
        return post;
    }

    @Override
    public Post update(Post post) {
        String updateSql = "UPDATE posts SET content = ?, updated = ?, status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setString(1, post.getContent());
            statement.setTimestamp(2, Timestamp.valueOf(post.getUpdated()));
            statement.setString(3, post.getStatus().toString());
            statement.setLong(4, post.getId());
            statement.executeUpdate();

            deletePostLabels(post.getId());
            savePostLabels(post);
            connection.commit();

        } catch (SQLException e) {
            rollbackQuietly();
            throw new RuntimeException("Failed to update post", e);
        }
        return post;
    }

    @Override
    public Post getById(Long id) {
        String sql = """
            SELECT
                p.id        AS post_id,
                p.writer_id AS writer_id,
                p.content   AS content,
                p.created   AS created,
                p.updated   AS updated,
                p.status    AS status,
                l.id        AS label_id,
                l.name      AS label_name
            FROM posts p
            LEFT JOIN post_labels pl ON p.id = pl.post_id
            LEFT JOIN labels l ON l.id = pl.label_id
            WHERE p.id = ?
        """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                Post post = null;
                List<Label> labels = new ArrayList<>();

                while (resultSet.next()) {
                    if (post == null) {
                        post = new Post();
                        post.setId(resultSet.getLong("post_id"));

                        long writerId = resultSet.getLong("writer_id");
                        post.setWriterId(resultSet.wasNull() ? null : writerId);

                        post.setContent(resultSet.getString("content"));

                        Timestamp created = resultSet.getTimestamp("created");
                        if (created != null) post.setCreated(created.toLocalDateTime());

                        Timestamp updated = resultSet.getTimestamp("updated");
                        if (updated != null) post.setUpdated(updated.toLocalDateTime());

                        post.setStatus(PostStatus.valueOf(resultSet.getString("status")));
                    }

                    long labelId = resultSet.getLong("label_id");
                    if (!resultSet.wasNull()) {
                        labels.add(new Label(labelId, resultSet.getString("label_name")));
                    }
                }

                if (post != null) {
                    post.setLabels(labels);
                }

                connection.commit();
                return post;
            }
        } catch (SQLException e) {
            rollbackQuietly();
            throw new RuntimeException("Failed to get post by id=" + id, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            deletePostLabels(id);
            statement.setLong(1, id);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollbackQuietly();
            throw new RuntimeException("Failed to delete post id=" + id, e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Post post = mapResultSetToPost(resultSet);
                post.setLabels(getLabelsByPostId(post.getId()));
                posts.add(post);
            }
            connection.commit();
        } catch (SQLException e) {
            rollbackQuietly();
            throw new RuntimeException("Failed to get all posts", e);
        }
        return posts;
    }


    private void savePostLabels(Post post) throws SQLException {
        if (post.getLabels() == null || post.getLabels().isEmpty()) return;

        String sql = "INSERT INTO post_labels (post_id, label_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Label label : post.getLabels()) {
                statement.setLong(1, post.getId());
                statement.setLong(2, label.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void deletePostLabels(Long postId) throws SQLException {
        String sql = "DELETE FROM post_labels WHERE post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, postId);
            statement.executeUpdate();
        }
    }

    private List<Label> getLabelsByPostId(Long postId) throws SQLException {
        List<Label> labels = new ArrayList<>();
        String sql = """
            SELECT l.id, l.name 
            FROM labels l 
            JOIN post_labels pl ON l.id = pl.label_id 
            WHERE pl.post_id = ?
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, postId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                labels.add(new Label(rs.getLong("id"), rs.getString("name")));
            }
        }
        return labels;
    }

    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        long writerId = rs.getLong("writer_id");
        post.setWriterId(rs.wasNull() ? null : writerId);
        post.setContent(rs.getString("content"));

        Timestamp created = rs.getTimestamp("created");
        if (created != null) post.setCreated(created.toLocalDateTime());

        Timestamp updated = rs.getTimestamp("updated");
        if (updated != null) post.setUpdated(updated.toLocalDateTime());

        post.setStatus(PostStatus.valueOf(rs.getString("status")));
        return post;
    }

    private void rollbackQuietly() {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // ignore rollback failure
        }
    }
}
