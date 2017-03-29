# Yet Another Github Graph

The purpose of this project is to fetch arbitrary data from GitHub, parse it, and be able to present it as Directed Cyclic Graph

Using:
* [CytoscapeJS](http://js.cytoscape.org/) for presentation
* [SpringBoot](https://projects.spring.io/spring-boot/) for serving data

## Example results
From [single file](example/datga/ob_events.json) :

![Events graph](readme/events.png)


## Installation

```
./gradlew bootRun
```


## Docker
```
docker build -t yagg . && docker run -p 8080:8080 -t yagg
```

## Development
This project uses SpringBoot DevTools<br />
If you'r using JetBrains IDE, remember to set
`compiler.automake.allow.when.app.running=true` in the Registry and `Make project automatically` in the Compiler settings


## License
MIT

