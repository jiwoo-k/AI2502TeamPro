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
         <div class="selectedTag tagName" style="color:${addedTag.color}; border: 2px solid ${addedTag.color}">
            <input name="name" type="hidden" value="${addedTag.name}">
            <input name="category_id" type="hidden" value="${addedTag.category_id}">
            <input name="id" type="hidden" value="${addedTag.id}">
            <input name="deleteTagColor" type="hidden" value="${addedTag.color}">
            <span th:style="color:${addedTag.color}"># ${addedTag.name}</span>
            <button class="deleteTag" th:style=" color: ${addedTag.color}">X</button>
         </div>
                    `);
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
        return
    }

    if($("input[name='sizeOverError']").val()){
        alert('태그는 최대 5개까지 담을 수 있습니다.');
        return;
    }

    $('#tagList').on('click', 'button.deleteTag', function(){
        if(confirm('해당 태그를 목록에서 삭제하시겠습니까?')){

            // 삭제할 div 요소
            const $tagDiv = $(this).closest('.selectedTag');

            // 태그 정보 추출
            let deleteTagName = $tagDiv.find("input[name='tagName']").val();
            let deleteCategoryId = $tagDiv.find("input[name='categoryId']").val();
            let deleteTagId = $tagDiv.find("input[name='tagId']").val();


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

                    }
                })

            $(this).parent().remove();
        }
    });

});
$(function(){
    // ... (기존 코드)

    $('button#tagSearchButton').click(function(event) {
        event.preventDefault(); // form submit 방지

        const tagName = $("input[name='name']").val();
        const categoryId = $("input[name='category_id']").val();

        fetch('/tag/search', { // 새로운 검색 API 엔드포인트
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
            },
            body: new URLSearchParams({
                name: tagName,
                category_id: categoryId
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data) {
                    // 검색 성공 시 UI 업데이트 (예: 검색 결과 영역에 보여주기)
                    $('.searchSucceed').text('검색 성공! 추가 가능합니다.');
                    // 검색된 태그 정보를 어딘가에 저장하거나 바로 추가 버튼 활성화 등의 처리
                    // 예시:
                    // $('#addSearchedTag').data('tag', data);
                    // $('#tagAddButton').prop('disabled', false);
                } else {
                    $('.searchSucceed').text(''); // 검색 실패 메시지 처리
                    alert('검색된 태그가 없습니다.');
                }
            });
    });

    // ... (나머지 JavaScript 코드 - tagAddButton 클릭 이벤트 등은 그대로 두세요)
});


