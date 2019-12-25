export PROJECTNAME=$(shell basename "$(PWD)")

.SILENT: ;               # no need for @

dynamo-local: ## Start local dynamo database
	docker-compose up -d dynamo

setup-local: dynamo-local ## Setup local environment (DynamoDb tables etc)
	aws dynamodb create-table --cli-input-json file://infra/create-print-documents-table.json --endpoint-url http://localhost:8000

run-local: # Runs the service locally connecting with dynamodb in Docker
	APP_DYNAMO=http://localhost:8000 MICRONAUT_ENVIRONMENTS=dev ./gradlew run

test-local: # Test the service locally connecting with dynamodb in Docker
	./gradlew test

run-sam-local: assemble ## Startup SAM locally
	sam local start-api --template sam.yaml --docker-network print-rider_default

assemble: ## Gradle Assemble
	./gradlew assemble

deploy-sls-infra: assemble ## Deploy infrastructure with Serverless framework
	sls --config infra/serverless.yml deploy -v

remove-sls-infra: ## Remove infrastructure with Serverless framework
	sls --config infra/serverless.yml remove -v

deploy-sls-function: assemble ## Deploy function with Serverless framework
	sls --config infra/serverless.yml deploy function -f print-rider

.PHONY: help
.DEFAULT_GOAL := help

help: Makefile
	echo
	echo " Choose a command run in "$(PROJECTNAME)":"
	echo
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
	echo