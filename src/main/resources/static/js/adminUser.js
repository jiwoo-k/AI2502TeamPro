$(document).ready(function() {
    $('select#warningCount').on('change', function() {
        $('form#warnCountForm').submit();
    });

    $('button.checkPause').click(function() {
       if(confirm('해당 계정을 정지하시겠습니까?')){
           alert('정지되었습니다.');
           $('form#pause3Form').submit();
       }
    });

    $('button.checkBan').click(function() {
        if(confirm('해당 계정을 영구 정지하시겠습니까?')){
            alert('영구 정지되었습니다.');
            $('form#pause5Form').submit();
        }
    });

    $('button.followReset').click(function() {
       if(confirm('해당 계정의 팔로워를 초기화하시겠습니까?')){
           alert('초기화되었습니다.');
           $('form#banForm').submit();
       }
    });
});