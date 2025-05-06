package com.bankingapp.banking.controller

import com.bankingapp.banking.repository.GroupsRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/groups/v1")
class GroupController (val groupsRepository: GroupsRepository) {


}