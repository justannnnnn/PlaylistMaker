package com.example.playlistmaker.data.dto

class TrackSearchResponse(val searchType: String,
                          val expr: String,
                          val results: ArrayList<TrackDto>) : Response()