package com.bankingapp.banking.controller

import com.bankingapp.banking.dto.*
import com.bankingapp.banking.repository.GroupsRepository
import com.bankingapp.banking.services.GroupService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/groups/v1")
class GroupController (
    val groupService: GroupService
) {

    @RestController
    @RequestMapping("/groups/v1")
    class GroupController (
        val groupService: GroupService
    ) {

        // get user's groups
        @GetMapping("/userGroups")
        fun getUserGroups(
            request: HttpServletRequest
        ): ResponseEntity<*> {
            try {
                val userId = request.getAttribute("userId") as Long
                return ResponseEntity.ok(groupService.getUserGroups(userId))
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(mapOf("error" to e.message))
            }
        }

        // create group
        @PostMapping("/create")
        fun createGroup(
            request: HttpServletRequest,
            @RequestBody groupDto: GroupDto
        ): ResponseEntity<*> {
            try {
                val userId = request.getAttribute("userId") as Long
                return ResponseEntity.ok(groupService.createGroup(userId, groupDto))
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(mapOf("error" to e.message))
            }
        }

        // adding member
        @PostMapping(("/addMember"))
        fun addMemberToGroup(
            request: HttpServletRequest,
            @RequestBody addMemberRequest: AddGroupMemberRequestDTO
        ): ResponseEntity<*> {
            try {
                val adminId = request.getAttribute("userId") as Long //


        val addedMember = groupService.addGroupMember(
            adminId,
            addMemberRequest.groupId,
            addMemberRequest.userIdToAdd
        )
        return ResponseEntity.ok(addedMember)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(mapOf("error" to e.message))
            }
        }

        @PostMapping("/payment")
        fun payForGroup(
            request: HttpServletRequest,
            @RequestBody paymentDTO: GroupPaymentDTO
        ): ResponseEntity<*> {
            try {
                val adminId = request.getAttribute("userId") as Long
                return ResponseEntity.ok(groupService.payForGroup(
                    adminId,
                    paymentDTO
                ))
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(mapOf("error" to e.message))
            }
        }

        @PostMapping(("/removeMember"))
        fun removeGroupMember(
            request: HttpServletRequest,
            @RequestBody removeMemberRequest: RemoveGroupMemberRequestDTO
        ): ResponseEntity<*> {
            try {
                val adminId = request.getAttribute("userId") as Long

                return ResponseEntity.ok(groupService.removeGroupMember(
                    adminId,
                    removeMemberRequest.groupId,
                    removeMemberRequest.userIdToRemove
                ))
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(mapOf("error" to e.message))
            }
        }

        @PostMapping("/de-activate-group")
        fun deActivateGroup(
            request: HttpServletRequest,
            @RequestBody deActivateGroupRequestDTO: deActivateGroupRequestDTO
        ): ResponseEntity<*> {
            try {
                val adminId = request.getAttribute("userId") as Long
                return ResponseEntity.ok(groupService.deActivateGroup(adminId, deActivateGroupRequestDTO.groupId))
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(mapOf("error" to e.message))
            }
        }

        // get group details
        @PostMapping("/details")
        fun getGroupDetails(
            request: HttpServletRequest,
            @RequestBody groupRequest: GroupIdRequestDTO
        ): ResponseEntity<*> {
            try {
                val userId = request.getAttribute("userId") as Long
                return ResponseEntity.ok(groupService.getGroupDetails(userId, groupRequest.groupId))
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(mapOf("error" to e.message))
            }
        }

        @PostMapping("/fund-requests")
        fun adminFundRequest(
            request: HttpServletRequest,
            @RequestBody adminRequest: adminFundRequestDTO,
            @RequestAttribute userId: Long
        ){
            groupService.adminFundRequest(userId, adminRequest)
        }


    }


}