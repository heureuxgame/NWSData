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
https://api.weather.gov/gridpoints/LSX/94,73/forecast/ 
from properties:  forecast of the response
Use this as your BASE_URL in NwsApiService.kt
that is your forecast gridpoint

There are some issues, the returns are not always in the same format as the documention
ApiData.kt is an example of the data returned from the gridpoint call

The icons in the drawables folder are from https://www.weather.gov/forecast-icons
The drawable's filename for the icon doesn't always match the url provided in the JSON return
https://api.weather.gov/icons/land/night/few?size=small
so there is some parsing and work-around in the HomeViewModel.kt file
