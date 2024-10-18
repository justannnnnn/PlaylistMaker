package com.example.playlistmaker.data.search.dto

import com.example.playlistmaker.data.Response

class TrackSearchResponse(val searchType: String,
                          val expr: String,
                          val results: ArrayList<TrackDto>) : Response()