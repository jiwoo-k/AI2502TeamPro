$(function () {
    const $categories = $('div.categoryBox');

    // 카테고리 선택
    $categories.on('click', function () {
        $categories.removeClass('selected');
        $(this).addClass('selected');

        const categoryId = Number($(this).attr('id'));
        $("input[name='category_id']").val(categoryId);
    });

    // 태그 검색
    $('#tagSearchButton').click(function () {
        const name = $('input[name="tagName"]').val();
        const categoryId = Number($('input[name="category_id"]').val());

        if (!name || !categoryId) {
            alert("태그명과 카테고리를 입력해주세요.");
            return;
        }

        $.ajax({
            url: '/tag/search',
            type: 'POST',
            data: {name: name, category_id: categoryId},
            success: function (tag) {
                $('#tagAddButton').hide();

                const newTag = `
                    <div class="selectedTag">
                        <input name="tagName" type="hidden" value="${tag.name}">
                        <input name="categoryId" type="hidden" value="${tag.category_id}">
                        <input name="tagId" type="hidden" value="${tag.id}">
                        <span style="background-color:${tag.color}"># ${tag.name}</span>
                        <button class="deleteTag">X</button>
                    </div>
                `;
                $('#tagList').append(newTag);
                $('.searchSucceed').text("검색 성공! 목록에 추가되었습니다.").show();
            },
            error: function (xhr) {
                if (xhr.status === 404) {
                    alert(categoryId + " 해당 태그가 존재하지 않습니다.");
                    $('#tagAddButton').show();
                } else {
                    alert("태그 검색 중 오류 발생");
                }
            }
        });
    });

    // 태그 추가 (신규 태그 생성 및 목록 저장)
    $('#tagAddButton').click(function (event) {
        event.preventDefault();

        const categoryId = Number($("input[name='category_id']").val());
        const tagName = $("input[name='tagName']").val();

        if (!categoryId || !tagName) {
            alert("카테고리와 태그명을 입력하세요.");
            return;
        }

        const addTagInfo = {
            name: tagName,
            category_id: categoryId
        };
        const requestBody = JSON.stringify(addTagInfo);
        fetch('/tag/save', {
            method: 'POST',
            headers: {"Content-Type": "application/json"},
            body: requestBody,
        })
            .then(response => response.json())
            .then(data => {
                if (data.alreadyInList) {
                    alert(data.alreadyInList);
                    history.back();
                    return;
                }
                if (data.sizeOverError) {
                    alert(data.sizeOverError);
                    history.back();
                    return;
                }
                if (data.addExistingTag) {
                    alert('검색 태그 목록에 저장 성공!');
                    return;
                }
                if (data.addTag) { // data.result 대신 data.addTag 존재 여부로 판단
                    const addedTag = data.addTag;
                    $('#tagList').append(`
               <div class="selectedTag">
                <input name="name" type="hidden" value="${addedTag.name}">
                <input name="category_id" type="hidden" value="${addedTag.category_id}">
                <input name="id" type="hidden" value="${addedTag.id}">
                <span style="background-color:${addedTag.color}"># ${addedTag.name}</span>
                <button class="deleteTag">X</button>
            </div>
            `);
                    if (data.result > 0) {
                        alert('신규 태그 생성 및 목록에 저장 성공!');
                    } else {
                        alert('기존 태그 목록에 추가 성공!');
                    }
                }
            });

    });

    // 태그 삭제 (이벤트 위임)
    $(document).on('click', 'button.deleteTag', function () {
        if (confirm('해당 태그를 목록에서 삭제하시겠습니까?')) {
            $(this).parent().remove();
        }
    });

// 폼 제출 직전에 실행
    $('#writeForm').on('submit', function (e) {
        // hiddenTagsContainer 비우기 (중복 방지)
        $('#hiddenTagsContainer').empty();

        // 화면에 보이는 선택 태그 각각에 대해 처리
        $('#tagList .selectedTag').each(function () {
            var tagId = $(this).find('input[name="tagId"]').val();
            if (tagId) {
                // hidden input 생성
                var input = $('<input>')
                    .attr('type', 'hidden')
                    .attr('name', 'tagIds[]')
                    .val(tagId);

                $('#hiddenTagsContainer').append(input);
            }
        });
    });

    // 페이지 로딩 시 에러 메시지 처리
    const isExistError = $("input[name='existError']").val();
    if (isExistError) {
        alert('목록에 이미 추가된 태그입니다.');
        history.back();
    }

    if ($("input[name='sizeOverError']").val()) {
        alert('태그는 최대 5개까지 담을 수 있습니다.');
        history.back();
    }
});



