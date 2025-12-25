package com.hsystudio.valtips.data.mapper

import com.hsystudio.valtips.domain.model.RoleFilterItem

/** 역할 정렬 순서를 지정하고, "전체" 역할을 첫 번째에 추가 */
fun List<RoleFilterItem>.organize(): List<RoleFilterItem> {
    // 역할 표시 순서 정의
    val roleOrder = listOf(
        "1b47567f-8f7b-444b-aae3-b0c634622d10", // 척후대
        "dbe8757e-9e92-4ed4-b39f-9dfc589691d4", // 타격대
        "5fc02f99-4091-4486-a531-98459a3e95e9", // 감시자
        "4ee40330-ecdd-4f2f-98a8-eb1243428373"  // 전력가
    )

    // 지정된 순서에 맞게 정렬
    val ordered = this.sortedWith(
        compareBy {
            val idx = roleOrder.indexOf(it.uuid)
            if (idx == -1) Int.MAX_VALUE else idx
        }
    )

    // "전체" 필터를 맨 앞에 추가하고 변환 결과 반환
    return listOf(RoleFilterItem("", null)) + ordered.map {
        RoleFilterItem(
            uuid = it.uuid,
            roleIconLocal = it.roleIconLocal
        )
    }
}
