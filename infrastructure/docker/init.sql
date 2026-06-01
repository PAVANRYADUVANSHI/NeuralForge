SELECT 'CREATE DATABASE neuralforge' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'neuralforge')\gexec

DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'neuralforge_user') THEN
    CREATE USER neuralforge_user WITH ENCRYPTED PASSWORD 'neuralforge_password';
  END IF;
END
$$;

GRANT ALL PRIVILEGES ON DATABASE neuralforge TO neuralforge_user;
