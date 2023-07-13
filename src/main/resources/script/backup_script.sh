#!/bin/bash

# MySQL Database Configuration
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="pos"
DB_USER="root"
DB_PASSWORD="Mjhome#123"

# Backup File Configuration
# Determine the backup file location based on the operating system
if [[ "$OSTYPE" == "darwin"* ]]; then
  # macOS
  source ~/.bash_profile
  BACKUP_FILE="/tmp/backup_$(date +%Y-%m-%d_%H-%M-%S).sql"
elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
  # Windows
  BACKUP_DIR="C:/tmp"
  mkdir -p "$BACKUP_DIR"  # Create directory if it doesn't exist
  BACKUP_FILE="$BACKUP_DIR/backup_$(date +%Y-%m-%d_%H-%M-%S).sql"
else
  # Other operating systems (fallback)
  BACKUP_FILE="/tmp/backup_$(date +%Y-%m-%d_%H-%M-%S).sql"
fi
# Create the backup file
mysqldump -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" > "$BACKUP_FILE"

# Check if backup was successful
if [ $? -eq 0 ]; then
  echo "$BACKUP_FILE"
else
  echo "Backup creation failed!"
fi

# Clean up temporary directory
rm -rf "$TEMP_DIR"