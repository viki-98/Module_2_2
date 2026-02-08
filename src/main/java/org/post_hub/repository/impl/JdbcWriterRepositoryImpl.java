package org.post_hub.repository.impl;

import org.post_hub.config.DatabaseUtil;
import org.post_hub.model.Post;
import org.post_hub.model.PostStatus;
import org.post_hub.model.Writer;
import org.post_hub.repository.WriterRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcWriterRepositoryImpl implements WriterRepository {

    @Override
    public Writer save(Writer writer) {
        String sql = "INSERT INTO writers (first_name, last_name) VALUES (?, ?)";
        try (PreparedStatement statement = DatabaseUtil.getPreparedStatementGetGeneratedKeys(sql)) {
            statement.setString(1, writer.getFirstName());
            statement.setString(2, writer.getLastName());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    writer.setId(generatedKeys.getLong(1));
                }
            }
            statement.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writer;
    }

    @Override
    public Writer update(Writer writer) {
        String sql = "UPDATE writers SET first_name = ?, last_name = ? WHERE id = ?";
        try (PreparedStatement statement = DatabaseUtil.getPreparedStatementWithoutAutoCommit(sql)) {
            statement.setString(1, writer.getFirstName());
            statement.setString(2, writer.getLastName());
            statement.setLong(3, writer.getId());
            statement.executeUpdate();
            statement.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writer;
    }

    @Override
    public Writer getById(Long id) {
        String sql = "SELECT * FROM writers WHERE id = ?";
        try (PreparedStatement statement = DatabaseUtil.getPreparedStatementWithoutAutoCommit(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Writer writer = mapResultSetToWriter(resultSet);
                writer.setPosts(getPostsByWriterId(writer.getId()));
                return writer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM writers WHERE id = ?";
        try (PreparedStatement statement = DatabaseUtil.getPreparedStatementWithoutAutoCommit(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
            statement.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Writer> getAll() {
        List<Writer> writers = new ArrayList<>();
        String sql = "SELECT * FROM writers";
        try (PreparedStatement statement = DatabaseUtil.getPreparedStatementWithoutAutoCommit(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Writer writer = mapResultSetToWriter(resultSet);
                writer.setPosts(getPostsByWriterId(writer.getId()));
                writers.add(writer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return writers;
    }


    private Writer mapResultSetToWriter(ResultSet rs) throws SQLException {
        Writer writer = new Writer();
        writer.setId(rs.getLong("id"));
        writer.setFirstName(rs.getString("first_name"));
        writer.setLastName(rs.getString("last_name"));
        return writer;
    }

    private List<Post> getPostsByWriterId(Long writerId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE writer_id = ?";
        try (PreparedStatement statement = DatabaseUtil.getPreparedStatementWithoutAutoCommit(sql)) {
            statement.setLong(1, writerId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getLong("id"));
                post.setContent(rs.getString("content"));

                Timestamp created = rs.getTimestamp("created");
                if (created != null) post.setCreated(created.toLocalDateTime());

                Timestamp updated = rs.getTimestamp("updated");
                if (updated != null) post.setUpdated(updated.toLocalDateTime());

                post.setStatus(PostStatus.valueOf(rs.getString("status")));

                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }
}
