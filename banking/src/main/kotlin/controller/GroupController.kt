package com.bankingapp.banking.controller

import com.bankingapp.banking.dto.*
import com.bankingapp.banking.repository.GroupsRepository
import com.bankingapp.banking.services.GroupService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/groups/v1")
class GroupController (
    val groupService: GroupService
) {

//    // create group
//    @PostMapping("/create")
//    fun createGroup(request: HttpServletRequest, @RequestBody groupDto: GroupDto): GroupResponseDTO {
//        val userId = request.getAttribute("userId") as Long
//        val createdGroup = groupService.createGroup(userId, groupDto)
//        return createdGroup
//    }
//
//    // adding member
//    @PostMapping(("/addMember"))
//    fun addMemberToGroup(
//        request: HttpServletRequest,
//        @RequestBody addMemberRequest: AddGroupMemberRequestDTO
//    ): ResponseEntity<*> {
//
//        val adminId = request.getAttribute("userId") as Long //
//
//
//        val addedMember = groupService.addGroupMember(
//            adminId,
//            addMemberRequest.groupId,
//            addMemberRequest.userIdToAdd
//        )
//        return ResponseEntity.ok(addedMember)
////        return GroupMemberResponseDTO(
////            id = addedMember.id,
////            userId = addedMember.userId,
////            groupId = addedMember.groupId,
////            isAdmin = addedMember.isAdmin,
////            joinedAt = addedMember.joinedAt
////        )
//    }
//
//    @PostMapping("/payment")
//    fun payForGroup(
//        request: HttpServletRequest,
//        @RequestBody paymentDTO: GroupPaymentDTO): ResponseEntity<*>
//    {
//        val adminId = request.getAttribute("userId") as Long
//        val payment = groupService.payForGroup(
//            adminId,
//            paymentDTO
//        )
//        return ResponseEntity.ok(payment)
//
//    }
//
//    @PostMapping(("/removeMember"))
//    fun removeGroupMember(
//        request: HttpServletRequest,
//        @RequestBody removeMemberRequest: RemoveGroupMemberRequestDTO,
//    ): userRemoved {
//        val adminId = request.getAttribute("userId") as Long
//        val removedMember = groupService.removeGroupMember(
//            adminId,
//            removeMemberRequest.groupId,
//            removeMemberRequest.userIdToRemove
//        )
//        return userRemoved(
//            groupId = removeMemberRequest.groupId,
//            RemovedUserId = removeMemberRequest.userIdToRemove
//        )
//    }
//
//    @PostMapping("/de-activate-group")
//    fun deActivateGroup(
//        request: HttpServletRequest,
//        @RequestBody deActivateGroupRequestDTO: deActivateGroupRequestDTO): ResponseEntity<*>
//    {
//        val adminId = request.getAttribute("userId") as Long
//
//        return ResponseEntity.ok(groupService.deActivateGroup(adminId, deActivateGroupRequestDTO.groupId))
//
//    }
//
//    //get group details
//    @PostMapping("/details")
//    fun getGroupDetails(request: HttpServletRequest, @RequestBody groupRequest: GroupIdRequestDTO): GroupDetailsDTO {
//        val userId = request.getAttribute("userId") as Long
//        return groupService.getGroupDetails(userId, groupRequest.groupId)
//
//    }



    @RestController
    @RequestMapping("/groups/v1")
    class GroupController (
        val groupService: GroupService
    ) {

        // create group
        @PostMapping("/create")
        fun createGroup(
            request: HttpServletRequest,
            @RequestBody groupDto: GroupDto
        ): Any {
            try {
                val userId = request.getAttribute("userId") as Long
                return groupService.createGroup(userId, groupDto)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(mapOf("error" to e.message))
            }
        }

        // adding member
        @PostMapping(("/addMember"))
        fun addMemberToGroup(
            request: HttpServletRequest,
            @RequestBody addMemberRequest: AddGroupMemberRequestDTO
        ): Any {
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
        ): Any {
            try {
                val adminId = request.getAttribute("userId") as Long
                return groupService.payForGroup(
                    adminId,
                    paymentDTO
                )
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(mapOf("error" to e.message))
            }
        }

        @PostMapping(("/removeMember"))
        fun removeGroupMember(
            request: HttpServletRequest,
            @RequestBody removeMemberRequest: RemoveGroupMemberRequestDTO
        ): Any {
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
        ): Any {
            try {
                val adminId = request.getAttribute("userId") as Long
                return groupService.deActivateGroup(adminId, deActivateGroupRequestDTO.groupId)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(mapOf("error" to e.message))
            }
        }

        // get group details
        @PostMapping("/details")
        fun getGroupDetails(
            request: HttpServletRequest,
            @RequestBody groupRequest: GroupIdRequestDTO
        ): Any {
            try {
                val userId = request.getAttribute("userId") as Long
                return groupService.getGroupDetails(userId, groupRequest.groupId)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(mapOf("error" to e.message))
            }
        }


    }


}