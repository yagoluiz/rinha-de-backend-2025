.PHONY: run-db stop-db logs-db run-adminer stop-adminer run-compose down-compose logs-compose-db

run-db:
	docker run --name postgres-db \
	    -e POSTGRES_USER=postgres \
	    -e POSTGRES_PASSWORD=postgres \
	    -e POSTGRES_DB=rinha_backend \
	    -v pgdata:/var/lib/postgresql/data \
	    -v $(PWD)/init.sql:/docker-entrypoint-initdb.d/init.sql \
	    -p 5432:5432 \
	    -d postgres:17.5-alpine

stop-db:
	docker rm -f postgres-db || true
	docker volume rm pgdata || true

logs-db:
	docker logs -f postgres-db

run-adminer:
	docker run --name adminer \
		--link postgres-db:db \
		-p 8080:8080 \
		-d adminer

stop-adminer:
	docker rm -f adminer || true

run-compose:
	docker-compose build && docker-compose up -d

down-compose:
	docker-compose down

logs-compose-db:
	docker-compose logs -f db
