// src/main/resources/static/js/comment.js
document.addEventListener("DOMContentLoaded", () => {
    const postId               = window.postId;
    const loggedUserId         = window.loggedUserId;
    const postAuthorId         = window.postAuthorId;
    const commentInput         = document.getElementById("input-comment-content");
    const commentBtn           = document.getElementById("btn-write-comment");
    const commentListContainer = document.getElementById("comment-list-container");
    const commentCountBadge    = document.getElementById("comment-count");

    // 대댓글 관련 요소
    const replyFormContainer   = document.getElementById("reply-form-container");
    const replyParentIdInput   = document.getElementById("replyParentId");
    const replyContentInput    = document.getElementById("replyContent");
    const cancelReplyBtn       = document.getElementById("btn-cancel-reply");

    if (!postId || !commentListContainer) return;

    // ─── 대댓글 폼 취소 ───────────────────────────────────
    cancelReplyBtn.addEventListener("click", () => {
        replyFormContainer.style.display = "none";
        replyContentInput.value = "";
    });

    // ─── 댓글 목록 조회 및 렌더링 ───────────────────────────
    async function loadComments() {
        try {
            const res = await fetch(`/comment/list/${postId}`);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            const { data: list } = await res.json();

            commentListContainer.innerHTML   = "";
            commentCountBadge.textContent    = list.length;
            replyFormContainer.style.display = "none";

            // ▶ 중첩된 replies 배열 처리
            list.forEach(c => {
                // ① 최상위 댓글 렌더
                const el = createCommentEl(c);
                el.id   = `comment-${c.id}`;
                commentListContainer.appendChild(el);

                // ② 이 댓글의 대댓글(replies) 렌더
                if (Array.isArray(c.replies)) {
                    c.replies.forEach(r => {
                        const childEl = createCommentEl(r);
                        childEl.classList.add("ms-4");
                        el.appendChild(childEl);
                    });
                }
            });

        } catch (e) {
            console.error(e);
            alert(`댓글을 불러오는 중 오류가 발생했습니다. (${e.message})`);
        }
    }

    // ─── 댓글/대댓글 작성 ─────────────────────────────────
    async function writeComment(event) {
        event?.preventDefault();
        let content  = commentInput.value.trim();
        let parentId = "";

        // 대댓글 모드 확인
        if (replyFormContainer.style.display === "block") {
            content  = replyContentInput.value.trim();
            parentId = replyParentIdInput.value;
        }
        if (!content) {
            alert("내용을 입력해주세요.");
            return;
        }

        const body = new URLSearchParams();
        body.append("postId", postId);
        body.append("content", content);
        if (parentId) body.append("parentId", parentId);

        try {
            const res  = await fetch("/comment/write", {
                method: "POST",
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body
            });
            const json = await res.json();
            if (json.status === "OK") {
                commentInput.value      = "";
                replyContentInput.value = "";
                await loadComments();
            } else {
                alert(json.message || "댓글 작성 실패");
            }
        } catch (e) {
            console.error(e);
            alert(`댓글 작성 중 오류가 발생했습니다. (${e.message})`);
        }
    }

    // ─── 댓글 수정 ───────────────────────────────────────
    window.editComment = function (commentId, oldContent) {
        const newContent = prompt("댓글 수정", oldContent);
        if (!newContent || newContent === oldContent) return;
        fetch(`/comment/update/${commentId}`, {
            method: "PUT",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: new URLSearchParams({content: newContent})
        })
            .then(r => r.json())
            .then(json => {
                if (json.status === "OK") loadComments();
                else alert(json.message);
            });
    };

    // ─── 댓글 삭제 ───────────────────────────────────────
    window.deleteComment = function (commentId) {
        if (!confirm("댓글을 삭제하시겠습니까?")) return;
        fetch(`/comment/delete/${commentId}`, {method: "DELETE"})
            .then(r => r.json())
            .then(json => {
                if (json.status === "OK") loadComments();
                else alert(json.message);
            });
    };

    // ─── PICK 토글 ───────────────────────────────────────
    window.togglePick = function (commentId, isPicked) {
        const url = isPicked
            ? `/comment/unpick/${commentId}`
            : `/comment/pick/${commentId}`;
        fetch(url, {method: "PUT"})
            .then(r => r.json())
            .then(json => {
                if (json.status === "OK") loadComments();
                else alert(json.message);
            });
    };

    // ─── 답글 버튼 클릭 처리 (이벤트 위임) ────────────────
    commentListContainer.addEventListener("click", e => {
        if (!e.target.classList.contains("reply-btn")) return;
        const pid = e.target.dataset.parentId;
        replyParentIdInput.value = pid;

        const commentEl = e.target.closest(".comment");
        commentEl.appendChild(replyFormContainer);

        replyFormContainer.style.display = "block";
        replyContentInput.focus();
    });

    // ─── 이벤트 바인딩 & 초기 로드 ─────────────────────────
    commentBtn.addEventListener("click", writeComment);
    replyFormContainer
        .querySelector("button[type=submit]")
        .addEventListener("click", writeComment);
    loadComments();
});

// ─── 댓글 요소 생성 헬퍼 ─────────────────────────────────
function createCommentEl(comment) {
    const el = document.createElement("div");
    el.className = "comment mb-2 p-2 border rounded";

    // (1) 피커 권한이 있고, 댓글 작성자가 로그인 사용자와 다를 때만 버튼 보이기
    const canPick = window.loggedUserId === window.postAuthorId
        && comment.user?.id !== window.loggedUserId;

    const pickButton = canPick
        ? `<button class="btn btn-sm ${comment.isPicked ? 'btn-success' : 'btn-outline-success'}"
                  onclick="togglePick(${comment.id}, ${comment.isPicked})">
           ${comment.isPicked ? '✔ PICK 해제' : '✔ PICK'}
       </button>`
        : (comment.isPicked ? `<span class="badge bg-success">✔ PICK</span>` : '');


    el.innerHTML = `
        <div>
            <strong>${escapeHTML(comment.user?.username || '알 수 없음')}</strong>
            <span class="text-muted small">${formatTime(comment.createdAt)}</span>
        </div>
        <div class="mt-1">${escapeHTML(comment.content)}</div>
        <div class="mt-2">
            ${comment.user?.id === window.loggedUserId
        ? `<button class="btn btn-sm btn-outline-secondary me-1"
                         onclick="editComment(${comment.id}, '${escapeHTML(comment.content)}')">수정</button>
                 <button class="btn btn-sm btn-outline-danger"
                         onclick="deleteComment(${comment.id})">삭제</button>`
        : ""}
            ${pickButton}
            <button class="btn btn-sm btn-link reply-btn"
                    data-parent-id="${comment.id}">답글</button>
        </div>
    `;
    return el;
}

// ─── 시간 포맷 헬퍼 ─────────────────────────────────────
function formatTime(iso) {
    const date = new Date(iso);
    return date.toLocaleString("ko-KR");
}

// ─── HTML 이스케이프 헬퍼 ───────────────────────────────
function escapeHTML(str) {
    return str.replace(/</g, "&lt;").replace(/>/g, "&gt;");
}
