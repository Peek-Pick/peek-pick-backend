#!/bin/bash

echo "⏳ Waiting for PostgreSQL to finish initializing..."

until PGPASSWORD=$DB_PASSWORD psql -h db -U ppdbuser -d peek_pick_db -c "SELECT 1 FROM tbl_admin LIMIT 1;" > /dev/null 2>&1; do
  echo "📭 Still waiting for tbl_admin..."
  sleep 2
done

echo "✅ PostgreSQL initialized. Starting app."
exec java -jar /app.jar
