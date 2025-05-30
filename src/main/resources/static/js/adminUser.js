$(document).ready(function() {
    $('select#warningCount').on('change', function() {
        $('form#warnCountForm').submit();
    });

    $('button.checkPause3').click(function() {
       if(confirm('해당 계정을 3일 정지하시겠습니까?')){
           $(this).closest('form').submit();
           // alert('정지되었습니다.');
       }
    });

    $('button.checkPause7').click(function() {
        if(confirm('해당 계정을 7일 정지하시겠습니까?')){
            $(this).closest('form').submit();
            // alert('정지되었습니다.');
        }
    });

    $('button.checkBan').click(function() {
        if(confirm('해당 계정을 영구 정지하시겠습니까?')){
            $(this).closest('form').submit();
            // alert('영구 정지되었습니다.');
        }
    });

    $('button.followReset').click(function() {
       if(confirm('해당 계정의 팔로워를 초기화하시겠습니까?')){
           $(this).closest('form').submit();
           // alert('초기화되었습니다.');
       }
    });
});