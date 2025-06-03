#!/usr/bin/env bash

HOST="$1"
PORT="$2"
shift 2
CMD="$@"

until mysql -h"$DB_HOST" -u"$DB_USER" -p"$DB_PASS" -e "SELECT 1;" "$DB_NAME"; do
  echo "Waiting for MySQL $HOST:$PORT..."
  sleep 3
done

echo "$HOST:$PORT is available, starting app..."

TABLE_COUNT=$(mysql -h"$DB_HOST" -u"$DB_USER" -p"$DB_PASS" -N -s -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '$DB_NAME';")

if [ "$TABLE_COUNT" -eq 0 ]; then
  echo "Import Dump..."
  gunzip -c /usr/local/bin/Dump20250601.sql.gz | mysql -h"$DB_HOST" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME"
else
  echo "DB is already imported ($TABLE_COUNT Tables), skip import."
fi

echo "Starting app..."
exec $CMD
