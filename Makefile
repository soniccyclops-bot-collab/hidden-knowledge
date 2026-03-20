SHELL := /bin/bash
.DEFAULT_GOAL := help
.ONESHELL:
.SILENT:

##@ Setup
.PHONY: setup
setup: deps build ## Setup the development environment

.PHONY: deps
deps: ## Install dependencies
	lein deps

##@ Development
.PHONY: build
build: ## Build ClojureScript
	lein cljsbuild once dev

.PHONY: watch
watch: ## Watch and rebuild ClojureScript
	lein cljsbuild auto dev

.PHONY: server
server: ## Start the backend server
	lein ring server-headless

.PHONY: run
run: build server ## Build and run the application

.PHONY: dev
dev: ## Start development mode (server + watch in parallel)
	trap 'kill %1 %2' EXIT
	lein ring server-headless &
	lein cljsbuild auto dev &
	wait

##@ Research Tools
.PHONY: test-query
test-query: ## Test a research query
	curl -X POST http://localhost:3000/api/research \
		-H "Content-Type: application/json" \
		-d '{"question":"How did they magnetize the first magnets?"}'

.PHONY: test-search
test-search: ## Test Internet Archive search
	curl "http://localhost:3000/api/search?q=magnetism%20lodestone&year-start=1600&year-end=1800"

##@ Utilities
.PHONY: clean
clean: ## Clean build artifacts
	lein clean
	rm -rf resources/public/js/out
	rm -f resources/public/js/app.js

.PHONY: help
help: ## Display this help
	awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m<target>\033[0m\n"} /^[a-zA-Z_-]+:.*?##/ { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)