COMPOSE=docker compose

.PHONY: help build up up-build up-d down stop restart logs logs-java logs-go logs-python logs-db ps clean

help:
	@echo "Available commands:"
	@echo "  make build        - Build all Docker images"
	@echo "  make up           - Start all services"
	@echo "  make up-build     - Build and start all services with rebuild"
	@echo "  make up-d         - Start all services in detached mode"
	@echo "  make down         - Stop and remove containers"
	@echo "  make stop         - Stop containers without removing them"
	@echo "  make restart      - Restart all services with rebuild"
	@echo "  make logs         - Show logs for all services"
	@echo "  make logs-java    - Show Java service logs"
	@echo "  make logs-go      - Show Go service logs"
	@echo "  make logs-python  - Show Python service logs"
	@echo "  make logs-db      - Show PostgreSQL logs"
	@echo "  make ps           - Show running containers"
	@echo "  make clean        - Remove containers, volumes and orphans"

build:
	$(COMPOSE) build

up:
	$(COMPOSE) up

up-build:
	$(COMPOSE) up --build

up-d:
	$(COMPOSE) up -d

down:
	$(COMPOSE) down

stop:
	$(COMPOSE) stop

restart:
	$(COMPOSE) down
	$(COMPOSE) up --build

logs:
	$(COMPOSE) logs -f

logs-java:
	$(COMPOSE) logs -f java-service

logs-go:
	$(COMPOSE) logs -f go-service

logs-python:
	$(COMPOSE) logs -f python-service

logs-db:
	$(COMPOSE) logs -f db

ps:
	$(COMPOSE) ps

clean:
	$(COMPOSE) down -v --remove-orphans