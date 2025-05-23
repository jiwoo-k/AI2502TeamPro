document.addEventListener("DOMContentLoaded", () => {
    const postId = window.postId;
    const loggedUserId = window.loggedUserId;
    const postAuthorId = window.postAuthorId;
    const commentInput = document.getElementById("input-comment-content");
    const commentBtn = document.getElementById("btn-write-comment");
    const commentListContainer = document.getElementById("comment-list-container");
    const commentCountBadge = document.getElementById("comment-count");

    if (!postId || !commentBtn) return;

    async function loadComments() {
        try {
            const res = await fetch(`/comment/list/${postId}`);
            console.log('▶ 댓글 리스트 호출:', `/comment/list/${postId}`, '응답 상태:', res.status);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);

            const json = await res.json();
            const list = json.data;

            commentListContainer.innerHTML = "";
            commentCountBadge.textContent = list.length;

            list.forEach(comment => {
                const commentEl = document.createElement("div");
                commentEl.className = "comment mb-2 p-2 border rounded";

                const pickButton = (loggedUserId === postAuthorId)
                    ? `<button class="btn btn-sm ${comment.isPicked ? 'btn-success' : 'btn-outline-success'}" 
                    onclick="togglePick(${comment.id}, ${comment.isPicked})">
                  ${comment.isPicked ? '✔ PICK 해제' : '✔ PICK'}
              </button>`
                    : (comment.isPicked ? `<span class="badge bg-success">✔ PICK</span>` : '');

                commentEl.innerHTML = `
          <div>
            <strong>${comment.user?.username || '알 수 없음'}</strong>
            <span class="text-muted small">${formatTime(comment.createdAt)}</span>
          </div>
          <div class="mt-1">${comment.content}</div>
          <div class="mt-2">
            ${comment.user?.id === loggedUserId ? `
              <button class="btn btn-sm btn-outline-secondary me-1" onclick="editComment(${comment.id}, '${escapeHTML(comment.content)}')">수정</button>
              <button class="btn btn-sm btn-outline-danger" onclick="deleteComment(${comment.id})">삭제</button>
            ` : ''}
            ${pickButton}
          </div>
        `;

                commentListContainer.appendChild(commentEl);
            });

        } catch (e) {
            alert(`댓글을 불러오지 못했습니다. (${e.message})`);
            console.error(e);
        }
    }

    async function writeComment() {
        const content = commentInput.value.trim();
        if (!content) return alert("내용을 입력해주세요.");

        const body = new URLSearchParams({postId, content});

        const res = await fetch("/comment/write", {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body
        });

        const json = await res.json();
        if (json.status === "OK") {
            commentInput.value = "";
            await loadComments();
        } else {
            alert(json.message || "댓글 작성 실패");
        }
    }

    window.editComment = function (commentId, oldContent) {
        const newContent = prompt("댓글 수정", oldContent);
        if (!newContent || newContent === oldContent) return;

        fetch(`/comment/update/${commentId}`, {
            method: "PUT",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: new URLSearchParams({content: newContent})
        }).then(r => r.json())
            .then(json => {
                if (json.status === "OK") loadComments();
                else alert(json.message);
            });
    };

    window.deleteComment = function (commentId) {
        if (!confirm("댓글을 삭제하시겠습니까?")) return;

        fetch(`/comment/delete/${commentId}`, {method: "DELETE"})
            .then(r => r.json())
            .then(json => {
                if (json.status === "OK") loadComments();
                else alert(json.message);
            });
    };

    window.togglePick = function (commentId, isPicked) {
        const url = isPicked ? `/comment/unpick/${commentId}` : `/comment/pick/${commentId}`;
        fetch(url, {method: "PUT"})
            .then(r => r.json())
            .then(json => {
                if (json.status === "OK") loadComments();
                else alert(json.message);
            });
    };

    function formatTime(iso) {
        const date = new Date(iso);
        return date.toLocaleString("ko-KR");
    }

    function escapeHTML(str) {
        return str.replace(/</g, "&lt;").replace(/>/g, "&gt;");
    }

    commentBtn.addEventListener("click", writeComment);
    loadComments();
});
