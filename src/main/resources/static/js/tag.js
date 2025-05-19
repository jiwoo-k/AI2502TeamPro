$(function(){
    const $categories = $('div.categoryBox');

    $categories.on('click', function(){
        $categories.removeClass('selected');
        $(this).addClass('selected');

        //해당 카테고리 id 를 넘겨주자
        $("input[name='categoryId']").val($(this).attr('id'));
    })
})