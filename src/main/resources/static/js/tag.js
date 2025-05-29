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
        event.preventDefault();
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
                    return;
                }

                if(data.sizeOverError){
                    alert(data.sizeOverError);
                    return;
                }

                if(data.addTag){ // 서버에서 추가된 태그 정보를 받았다면
                    const addedTag = data.addTag;
                    document.querySelector("input[name='name']").value = "";
                    // 현재 태그 목록에 추가하는 로직
                    $('#tagList').append(`
         <div class="selectedTag tagName" style="color:${addedTag.color}; border: 1px solid ${addedTag.color}">
            <input name="name" type="hidden" value="${addedTag.name}">
            <input name="category_id" type="hidden" value="${addedTag.category_id}">
            <input name="id" type="hidden" value="${addedTag.id}">
            <input name="deleteTagColor" type="hidden" value="${addedTag.color}">
            <span th:style="color:${addedTag.color}"># ${addedTag.name}</span>
            <button class="deleteTag" th:style=" color: ${addedTag.color}">X</button>
         </div>
                    `);
                    return;
                }
                if(data.result > 0){
                    alert('신규 태그 생성 및 목록에 저장 성공!');
                    return;
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

    $('#tagList').on('click', 'button.deleteTag', function(){
        if(confirm('해당 태그를 목록에서 삭제하시겠습니까?')){

            // 삭제할 div 요소
            const $tagDiv = $(this).closest('.selectedTag');

            // 태그 정보 추출
            let deleteTagName = $tagDiv.find("input[name='name']").val();
            let deleteCategoryId = $tagDiv.find("input[name='category_id']").val();
            let deleteTagId = $tagDiv.find("input[name='id']").val();

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

});

function getTagList(){
    //HTML 에서 태그 갖고와서 배열로 전환
    const tagList = document.querySelectorAll("div.selectedTag");
    let tags = [];

    //핸들러에 전송할 tag 형 배열 생성
    tagList.forEach(tag => {
        const tagName = tag.querySelector("input[name='name']").value;
        const categoryId = tag.querySelector("input[name='category_id']").value;
        const tagId = tag.querySelector("input[name='id']").value;
        const tagColor = tag.querySelector("input[name='deleteTagColor']").value;

        tags.push({
            id: tagId,
            name: tagName,
            category_id: categoryId,
            color: tagColor,
        });
    });

    return tags;
}
