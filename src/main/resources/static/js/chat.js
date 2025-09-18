async function loadDirect(otherId){
    const res = await fetch(`/api/chat/direct/${otherId}`);
    const data = await res.json();
    const box = document.getElementById('messages');
    box.innerHTML = data.map(m => `<div>[${m.sentAt}] <b>${m.senderId}</b>: ${m.content}</div>`).join('');
}


async function sendDirect(){
    const otherId = new URLSearchParams(window.location.search).get('other');
    const content = document.getElementById('msg').value;
    await fetch('/api/chat/direct', {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({toUserId: Number(otherId), content})});
    document.getElementById('msg').value='';
    loadDirect(otherId);
}


async function loadGroup(groupId){
    const res = await fetch(`/api/chat/group/${groupId}`);
    const data = await res.json();
    const box = document.getElementById('groupMessages');
    box.innerHTML = data.map(m => `<div>[${m.sentAt}] <b>${m.senderId}</b>: ${m.content}</div>`).join('');
}


async function sendGroup(){
    const groupId = new URLSearchParams(window.location.search).get('group');
    const content = document.getElementById('gmsg').value;
    await fetch('/api/chat/group', {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({groupId: Number(groupId), content})});
    document.getElementById('gmsg').value='';
    loadGroup(groupId);
}