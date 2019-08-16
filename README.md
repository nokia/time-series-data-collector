# Time series data collector

Generic time series data collector / exporter.<br/>
Currently, only Prometheus is available as a data source.<br/>

It offers the following features:
- Get time series from a start timestamp to an end point.
- Get the last X seconds of a time series.
- Search for a time series of a finite size anywhere in the past.
- Get time series considering only a start point.
- Export the time series in JSON or CSV files via a shared volume or web services.

One of the main advantages of these features is that they don't have any size limit for the time series. If the time series database has its own limit, TSDC will split the queries automatically and reconstruct the whole time series.

The solution is containerized.

## Getting started

### Configure config.json
First, you need to set your data source -> resources/config.json
- <b>datasource.type</b>: The datasource you want to use. Currently, only the <b>prometheus</b> value is available.
- <b>datasource.srvaddress</b>: The IP:port of the datasource.
- <b>historytime</b>: The limit to get the data backward.
- <b>maxduration</b>: The limit to get the data forward.
- <b>output.json</b>: The output for the generated json files. If empty, the json files generation is disabled.
- <b>output.csv</b>: The output for the generated csv files. If empty, the csv files generation is disabled.

### Build the Doker image
```
docker build --tag=time-series-data-collector .
```
### Run the docker image
The data file generation is enabled by default. To disable it, empty output.json and output.csv field in the config.json file.
#### If you enabled the file generation (json/csv)
```
docker run -v /host/tsdc-data:/opt/tsdc-data --net=host time-series-data-collector
```
The tsdc-data directory will be generated into your /host, with 2 subdirectories: json and csv.<br/>
One json file will be generated for each time series, but only one csv file will be generated for a group of time series from the same query.
#### If you disabled the file generation
```
docker run --net=host time-series-data-collector
```
## API
| Method          | Path                        | Description   |
| :-------------: | :-------------------------: | :-----------: |
|  GET            | /collector/service/get_ts   | Get one or many time series via a query (e.g Prometheus query) |

### Parameters

| Parameter       | Required | Description |
| :-------------: | :-: | :-----------: |
|  query          | YES | The data source query (e.g Prometheus query). It's better to encode it before to use it into an URL. |
|  id             | NO  | It's possible to set a custom ID in order to name the generated files. If not used, the ID will be auto-generated. |
|  start          | NO  | The start timestamp (in seconds) of the time series. If not used, the collector will get the <b>historytime</b> last seconds of data |
|  end          | NO  | Works only if the <b>start</b> parameter is used. Gets the time series from <b>start</b> to <b>end</b>. if not used, the collector will get the data from <b>start</b> to <b>start</b>+<b>maxduration</b>. |
|  reducehttprequests | NO  | Useful or not considering the use-cases. If you're getting continuous time series, this parameter is useless. However, if you're looking for an isolated time series in your data source (e.g a build in a CI context), it will do only the minimum necessary http requests. Enabled by default. |

## Set up SSL

This solution is able to handle SSL connections.<br/>
The only thing you need to do is to:
- Generate your own keystore.jks
- Set the parameters in src/main/java/com/nokia/as/main/jetty/JettyConfig.java
- Add the port to expose in the Dockerfile (at the EXPOSE line)

## How it works

TSDC is based on range queries.<br/>
If the range is too big, the queries will be splitted automatically.<br/>
If one or both of the edges of the range are missing, here's the following cases:<br/>
- Only the start time is set
    - The http request optimizer is enabled:<br/>
    It will get the data until the maximum value assigned in the configuration file is reached. It the time series seems to be finished (e.g a build time series), the connections will stop.
    - The http request optimizer is disabled:<br/>
    It will get the data until the maximum value assigned in the configuration file is reached, no matter what the time series looks like.
- No edge is set
    - The http request optimizer is enabled:<br/>
    It will try get the data backward from the current timestamp. If there's no current data point, it will search for it until the hisory time assigned in the configuration file is reached. It it finds it, it will get the data backward. It the time series seems to be finished (e.g a build time series), the connections will stop.
    - The http request optimizer is disabled:<br/>
    Same thing, but if it finds a time series, it will continue to get the data backward, no matter what the time series looks like.

### Architecture schema

![tsdc-schema](https://user-images.githubusercontent.com/10490998/63107586-7ff79b80-bf85-11e9-844d-d61e26555390.png)

Icons made by [Smashicons](https://www.flaticon.com/authors/smashicons), 
[DinosoftLabs](https://www.flaticon.com/authors/dinosoftlabs), 
[Pixel Buddha](https://www.flaticon.com/authors/pixel-buddha) from [www.flaticon.com](https://www.flaticon.com) is licensed by [CC 3.0 BY](http://creativecommons.org/licenses/by/3.0/)
