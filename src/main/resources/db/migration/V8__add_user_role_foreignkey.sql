ALTER TABLE user_role
ADD FOREIGN KEY (user_id) REFERENCES users(id),
ADD FOREIGN KEY (role_id) REFERENCES role(id);