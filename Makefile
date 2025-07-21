.PHONY: run-db stop-db logs-db run-adminer stop-adminer run-compose down-compose logs-compose-db logs-compose-api-01 logs-compose-api-02

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
	docker-compose up -d --build

down-compose:
	docker-compose down

logs-compose-db:
	docker-compose logs -f db

logs-compose-api-01:
	docker-compose logs -f api-01

logs-compose-api-02:
	docker-compose logs -f api-02
