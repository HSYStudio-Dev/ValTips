package com.hsystudio.valtips.feature.setting

import com.hsystudio.valtips.data.auth.FakeRiotAccount

// 설정 화면에서 발생하는 "사용자 액션/트리거"를 한 곳에 모아둔 이벤트 집합
sealed interface SettingUiEvent {
    // "Riot ID 로그인" 버튼 클릭
    data object ClickRiotLogin : SettingUiEvent

    // "계정 추가" 버튼 클릭
    data object ClickAddAccount : SettingUiEvent

    // 딥링크로 로그인 토큰을 받았을 때
    data class OnLoginDeepLink(
        val token: String
    ) : SettingUiEvent

    // 비활성 계정 카드 클릭(계정 전환 시도)
    data class ClickAccountCard(
        val accountId: String
    ) : SettingUiEvent

    // 계정 카드에서 로그아웃 클릭
    data class ClickAccountLogout(
        val accountId: String
    ) : SettingUiEvent

    // 다이얼로그 "확인" 버튼 클릭
    data object ConfirmDialog : SettingUiEvent

    // 다이얼로그 "취소/닫기" 버튼 클릭
    data object DismissDialog : SettingUiEvent

    // "멤버십 관리/업그레이드" 버튼 클릭(멤버십 화면으로 이동)
    data object ClickMembershipManage : SettingUiEvent

    // "이용약관" 클릭(웹/앱내 화면 연결용)
    data object ClickTerms : SettingUiEvent

    // "개인정보처리방침" 클릭(웹/앱내 화면 연결용)
    data object ClickPrivacy : SettingUiEvent

    // "라이선스&저작권" 클릭(앱내 라이선스 화면 연결용)
    data object ClickLicenses : SettingUiEvent

    // DEV 옵션: 프로 멤버십 토글(테스트용 상태 전환)
    data class ToggleProMembership(
        val enabled: Boolean
    ) : SettingUiEvent

    // DEV 옵션: 온보딩 기록 삭제(테스트용)
    data object OnClickDeleteOnboarding : SettingUiEvent

    // DEV 옵션: 약관 동의 기록 삭제(테스트용)
    data object OnClickDeleteAgree : SettingUiEvent
}

// Setting 화면에서 "한 번만 실행"되어야 하는 동작(네비게이션/토스트 등)을 전달하는 이펙트 집합
sealed interface SettingUiEffect {
    // 멤버십 상세/결제 안내 화면으로 이동(라우터가 실제 네비게이션 수행)
    data object NavigateToMembership : SettingUiEffect

    // Chrome Custom Tabs 외부/내부 웹 페이지 열기
    data class OpenCustomTab(
        val url: String
    ) : SettingUiEffect

    // 토스트 메시지 표시
    data class ShowMessage(
        val message: String
    ) : SettingUiEffect
}

// Setting 화면에서 현재 떠 있는 다이얼로그의 "종류 + 필요한 데이터"를 표현하는 상태 모델
sealed interface SettingDialogState {
    // 계정 전환 다이얼로그(대상 계정 정보 포함)
    data class ConfirmSwitch(
        val targetAccount: FakeRiotAccount
    ) : SettingDialogState

    // 계정 로그아웃 다이얼로그(대상 계정 정보 포함)
    data class ConfirmLogout(
        val targetAccount: FakeRiotAccount
    ) : SettingDialogState

    // Todo : [임시] RSO 연동 전 임시 로그인/계정 추가 안내 다이얼로그
    data class DevLoginPrompt(
        val isAddAccount: Boolean
    ) : SettingDialogState
}
