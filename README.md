# Watch Next Service

Find your next movie to watch or create your own watchlist.  it should included reviews, ratings, actors and anything else you want to know about a movie.

The goal of this service is to help users rapidly find a movie to watch.

## Constraints:
*  This version of the services should be developed using Scala.
*  It should store the links in cassandra
*  It should provide a REST API

## User stories:
*  User should be able to list all the latest movies. (This year’s movies, This month movies, This week movies).
*  User should be able to get details about each movie.
*  User should be able to create a watchlist.
*  User should be able to search for a movie.
*  User should be able to see a list of links matching the search terms.
*  User should be able to store locally selected links to movies from the matching ones.
*  User should be able to get a suggestion list of movies to watch next but limited to max 10 movies.

## Delivery:
*  Source code 
*  instructions on how to build and run the service

## Useful links:
*  Imdb
*  The movies Database https://developers.themoviedb.org/3/getting-started/introduction

## How to run:

*  Open the terminal and go to the project folder
*  Execute "docker-compose up"
*  Execute "sbt compile" and then "sbt run"
*  Using curl or an app like Postman you can use the following API

## API:

*  `GET /latest` for a list of the latest movies
*  `POST /add` to add a new movie with the following payload:
    *  `id` : Movie ID from TheMovieDB
    *  `title` : Movie title
*  `GET /details/{movieID}` to get the details of a movie
*  `POST /details` to ge the details of multiple movies:
    *  `ids` : Array of Movie IDs
*  `GET /search` to search for a movie with the following query string:
    *  `q` : the search query
    *  example request: `/search?q=watchmen`
*  `GET /suggestions` to get a list of suggestions based on the movies you've watched
*  `PATCH /setWatched/{movieID}` to set a movie with the specified ID as watched