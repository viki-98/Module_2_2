package org.post_hub.repository.impl;

import org.post_hub.config.DatabaseConnection;
import org.post_hub.model.Label;
import org.post_hub.model.Post;
import org.post_hub.model.PostStatus;
import org.post_hub.repository.PostRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcPostRepositoryImpl implements PostRepository {
    private final Connection connection = DatabaseConnection.getConnection();

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

        } catch (SQLException e) {
            e.printStackTrace();
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public Post getById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Post post = mapResultSetToPost(resultSet);
                post.setLabels(getLabelsByPostId(post.getId()));
                return post;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteById(Long id) {
        deletePostLabels(id);

        String sql = "DELETE FROM posts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }


    private void savePostLabels(Post post) {
        if (post.getLabels() == null || post.getLabels().isEmpty()) return;

        String sql = "INSERT INTO post_labels (post_id, label_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Label label : post.getLabels()) {
                statement.setLong(1, post.getId());
                statement.setLong(2, label.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deletePostLabels(Long postId) {
        String sql = "DELETE FROM post_labels WHERE post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, postId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Label> getLabelsByPostId(Long postId) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return labels;
    }

    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setContent(rs.getString("content"));

        Timestamp created = rs.getTimestamp("created");
        if (created != null) post.setCreated(created.toLocalDateTime());

        Timestamp updated = rs.getTimestamp("updated");
        if (updated != null) post.setUpdated(updated.toLocalDateTime());

        post.setStatus(PostStatus.valueOf(rs.getString("status")));
        return post;
    }
}
