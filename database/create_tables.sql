DROP TABLE IF EXISTS app.todos;

DROP TABLE IF EXISTS app.tasks;

DROP TABLE IF EXISTS app.users;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE app.users (
  id UUID DEFAULT uuid_generate_v4(),
  name TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  PRIMARY KEY (id)
);

CREATE INDEX index_users_email ON app.users(email);

CREATE TABLE app.tasks (
  id UUID DEFAULT uuid_generate_v4(),
  name TEXT NOT NULL,
  description TEXT,
  created_at TIMESTAMP DEFAULT NOW(),
  user_id UUID NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES app.users(id) ON DELETE CASCADE
);

CREATE TABLE app.todos (
  id UUID DEFAULT uuid_generate_v4(),
  name TEXT NOT NULL,
  description TEXT,
  is_done BOOLEAN DEFAULT false,
  created_at TIMESTAMP DEFAULT now(),
  task_id UUID NOT NULL,
  user_id UUID NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_todos_task FOREIGN KEY (task_id) REFERENCES app.tasks(id) ON DELETE CASCADE,
  CONSTRAINT fk_todos_user FOREIGN KEY (user_id) REFERENCES app.users(id) ON DELETE CASCADE
);
