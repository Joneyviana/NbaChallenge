package com.example.nba.data.local.converter

import androidx.room.TypeConverter
import java.time.LocalDate

class DateConverters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let(LocalDate::parse)
    }
}