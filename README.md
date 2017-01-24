# SunshineJust

Put your Open Weather Map Api Key in app/build.gradle file. 

Replace VALUE with your API key.

 buildTypes.each {
        it.buildConfigField 'String', 'OPEN_WEATHER_MAP_API_KEY', '"VALUE"'
    }

ENSURE that you keep both quotes(SINGLE QUOTES FOLLOWED BY DOUBLE QUOTES)


