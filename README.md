# NWSData
NWS api hourly forecast app 

Documentation for the api can be found here: https://www.weather.gov/documentation/services-web-api

This app used a hardcoded url to fetch hourly forecast data for a specific nws gridpoint
see NwsApiService.kt in app

How do I determine the gridpoint for my location?
You can retrieve the metadata for a given latitude/longitude
coordinate with the /points endpoint (https://api.weather.gov/points/{lat},{lon}).
use only 4 decimals in lat_lon
For example, St Louis would be
https://api.weather.gov/points/38.6270,-90.1994
enter that URL in a web browser and from the response get
https://api.weather.gov/gridpoints/LSX/94,73/forecast/ as your BASE_URL
from properties:  forecast
this is your gridpoint

There are some issues, the returns are not always in the same format as the documention
ApiData.kt is an example of the data returned from the gridpoint call
