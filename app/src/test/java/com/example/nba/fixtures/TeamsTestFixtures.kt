package com.example.nba.fixtures

import com.example.nba.data.local.entity.TeamEntity
import com.example.nba.data.remote.dto.TeamDto
import com.example.nba.domain.model.Team

object TeamTestFixture {

    fun createTeamDto() = TeamDto(
        id = 1,
        full_name = "Los Angeles Lakers",
        city = "Los Angeles",
        conference = "West",
        name = "Lakers"
    )

    fun createTeamEntity() = TeamEntity(
        id = 1,
        fullName = "Los Angeles Lakers",
        city = "Los Angeles",
        conference = "West",
        name = "Lakers"
    )

    fun createTeam() = Team(
        id = 1,
        fullName = "Los Angeles Lakers",
        city = "Los Angeles",
        conference = "West"
    )
}