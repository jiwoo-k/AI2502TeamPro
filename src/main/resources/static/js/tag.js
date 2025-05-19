$(function (){
    let tagName;

    //검색 버튼 클릭: tag 테이블에서 찾아온다.
    $('#tagSearchButton').click(function (){
        tagName = $('input[name="name"]').val('');
    });
});