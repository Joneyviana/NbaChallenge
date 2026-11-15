package com.example.nba.fixtures

import com.example.nba.data.remote.dto.PlayerDto
import com.example.nba.data.remote.dto.TeamDto

object PlayerTestFixtures {

    fun createPlayerDto(): PlayerDto {
        return PlayerDto(
            id = 1,
            first_name = "LeBron",
            last_name = "James",
            team = TeamDto(
                id = 15,
                full_name = "Los Angeles Lakers",
                city = "Los Angeles",
                conference = "West",
                name = "Lakers"
            )
        )
    }
}