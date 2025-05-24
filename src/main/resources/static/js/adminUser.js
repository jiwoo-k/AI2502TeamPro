$(document).ready(function() {
    $('select#warningCount').on('change', function() {
        $('form#warnCountForm').submit();
    });

    $('button.checkPause').click(function() {
       if(confirm('해당 계정을 정지하시겠습니까?')){
           alert('정지되었습니다.');
       }
    });

    $('button.checkBan').click(function() {
        if(confirm('해당 계정을 영구 정지하시겠습니까?')){
            alert('영구 정지되었습니다.');
        }
    });
});