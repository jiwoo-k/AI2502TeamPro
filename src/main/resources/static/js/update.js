$(function () {
    let i = 0;

    $("#btnAdd").click(function () {
        $("#files").append(`
            <div class="input-group mb-2">
                <input class="form-control col-xs-3" type="file" name="upfile${i}" />
                <button type="button" class="btn btn-outline-danger" onclick="$(this).parent().remove()">삭제</button>
            </div>
        `);
        i++;
    });

    // 기존 파일 삭제 버튼 클릭 시
    $("[data-fileid-del]").click(function () {
        const fileId = $(this).attr("data-fileid-del");
        markFileForDeletion(fileId);
        $(this).parent().remove();
    });

    // 삭제할 파일 ID를 hidden input으로 추가
    function markFileForDeletion(fileId) {
        $("#delFiles").append(
            `<input type="hidden" name="delfile" value="${fileId}">`
        );
    }

    // [선택사항] Summernote 적용 (id가 있는 경우에만)
    if ($("#content").length > 0) {
        $("#content").summernote({
            height: 300,
        });
    }
});