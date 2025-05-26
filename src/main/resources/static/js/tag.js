$(function(){
    const $categories = $('div.categoryBox');

    $categories.on('click', function(){
        $categories.removeClass('selected');
        $(this).addClass('selected');

        //해당 카테고리 id 를 넘겨주자
        $("input[name='category_id']").val($(this).attr('id'));
    });

    $('button#tagAddButton').click(function (){
        let categoryId = $("input[name='category_id']").val();
        let tagName = $("input[name='name']").val();

        const addTagInfo = {
            name: tagName,
            category_id: categoryId,
        }

        const requestBody = new URLSearchParams(addTagInfo);

        fetch('/tag/add', {
            method: 'POST',
            headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
            body: requestBody,
        })
            .then(response => response.json())
            .then(data => {
                if(data.alreadyInList){
                    alert(data.alreadyInList);
                    history.back();
                }

                if(data.sizeOverError){
                    alert(data.sizeOverError);
                    history.back();
                }

                if(data.addExistingTag){
                    alert('검색 태그 목록에 저장 성공!');
                    location.href = data.url;
                }

                if(data.result > 0){
                    alert('신규 태그 생성 및 목록에 저장 성공!');
                    location.href = data.url;
                }
            });

    });


    let isExistError = $("input[name='existError']").val();
    if(isExistError){
        alert('목록에 이미 추가된 태그입니다.');
        history.back();
    }

    if($("input[name='sizeOverError']").val()){
        alert('태그는 최대 5개까지 담을 수 있습니다.');
        history.back();
    }

    $('button.deleteTag').click(function(){
        if(confirm('해당 태그를 목록에서 삭제하시겠습니까?')){
            let deleteTagName = $("div input[name = 'deleteTagName']").val();
            let deleteCategoryId = $("div input[name = 'deleteCategoryId']").val();
            let deleteTagId = $("div input[name = 'deleteTagId']").val();

            const removeTagInfo = {
                name: deleteTagName,
                category_id: deleteCategoryId,
                id: deleteTagId,
            }

            const requestBody = new URLSearchParams(removeTagInfo);

            fetch('/tag/remove', {
                method: 'POST',
                headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
                body: requestBody
            })
                .then(response => response.json())
                .then(data => {
                    if(data.deleteSuccess){
                        alert(data.deleteSuccess);
                        location.href = data.url;
                    }
                })

            $(this).parent().remove();
        }
    });

    $('button#saveTagList').click(function(){
        if(confirm('저장하시겠습니까?')){
            let tagList = $('div.selectedTag');
            //TODO: 저장 로직 작성

            alert('저장 성공');
        }
    });

});