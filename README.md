# Platea - Docker orchestrator

## Run the Postgresql database
```
docker run --name pgdb --env-file .env -P -p 5432:5432 -d postgres
```

## fetch-instances
	platea --fetch-instances
	fetching from the remote repository [url]...
## list-instances
	platea --list-instances
		- lcarnevale
		- config2
		- config3
## build
	platea build INSTANCE
## start
	platea start INSTANCE
## run
	platea run INSTANCE
## stop
	platea stop INSTANCE

# UML Scheme
![](UML.png)
