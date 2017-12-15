# start from postgres version 9.6
FROM postgres:9.6

# from https://docs.docker.com/samples/library/postgres/
# put some sql files in /docker-entrypoint-initdb.d (creating the directory if necessary).
# mkdir -p /docker-entrypoint-initdb.d

# Copy the current directory contents into the container at /app
COPY *.sql /docker-entrypoint-initdb.d