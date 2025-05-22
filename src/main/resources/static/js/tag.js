$(function(){
    const submitForm = document.getElementById("searchForm");
    const tagSearchButton = document.getElementById("tagSearchButton");

    if(submitForm && tagSearchButton){
        tagSearchButton.addEventListener('click', function() {
            const currentPageURL = window.location.pathname;
            submitForm.action = currentPageURL;
            submitForm.submit(); // 폼 제출
        });
    }

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
                    history.back();
                }

                if(data.result > 0){
                    alert('신규 태그 생성 및 목록에 저장 성공!');
                    history.back();
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
                        history.back();
                    }
                })

            $(this).parent().remove();
        }
    });

    $('button#saveTagList').click(function(){
        if(confirm('저장하시겠습니까?')){
            //1. 태그 목록 가져와서,
            let tagList = getTagList();

            //2. 서버로 전송
            fetch('/tag/save', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(tagList)
            })
                .then(response => response.json())


            alert('저장 성공');
        }
    });

});

function getTagList(){
    //HTML 에서 태그 갖고와서 배열로 전환
    const tagList = document.querySelectorAll("div.selectedTag");
    let tags = [];


    //핸들러에 전송할 tag 형 배열 생성
    tagList.forEach(tag =>{
        const tagName = tag.querySelector("input[name='deleteTagName']").val();
        const categoryId = tag.querySelector("input[name='deleteCategoryId']").val();
        const tagId = tag.querySelector("input[name='deleteTagId']").val();
        const tagColor = tag.querySelector("input[name='deleteTagColor']").val();

        tags.push({
            id: tagId,
            name: tagName,
            category_id: categoryId,
            color: tagColor,
        });
    });

    return tags;
}