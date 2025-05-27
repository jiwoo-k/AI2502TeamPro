$(function() {
    if($('input#locationInfo').val().trim()){
        alert("위치 정보를 설정 후 이용해주세요");
        location.href = "/home";
    }
})