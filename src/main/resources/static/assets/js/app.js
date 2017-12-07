var stompClient = null;
var audio = new Audio('/assets/sound/knock_brush.mp3');
var app = new Vue({
    el: '#main-app',
    data: {
        me: userData,
        users: {
            online: [],
            offline: []
        },
        selectedUser: undefined,
        messages: [],
        messageLoading: false,
        typingTask: undefined,
        message: ''
    },
    methods: {
        loadUsers: function () {
            this.$http.get('/api/user/list')
                .then(function (value) {
                    this.users = value.body
                    if (this.selectedUser) {
                        this.selectedUser = this.findById(this.selectedUser.id)
                    }
                }, function (reason) {
                    console.log(reason)
                })
        },
        loadMessages: function () {
            this.messages = []
            this.messageLoading = true
            this.$http.get('/api/message/user/' + this.selectedUser.id)
                .then(function (value) {
                    this.messages = value.body
                    this.messageLoading = false
                    this.scrollToLastMessage()
                }, function (reason) {
                    this.messageLoading = false
                    console.log(reason)
                })
        },
        onMessageType: function () {
            if (this.message.trim().length === 0 || !this.selectedUser.active) return;
            if (this.typingTask) {
                clearTimeout(this.typingTask)
                const $this = this;
                this.typingTask = setTimeout(function () {
                    stompClient.send("/app/typing", {}, JSON.stringify({
                        name: $this.selectedUser.email,
                        typing: false
                    }))
                    $this.typingTask = null
                }, 500)
            } else {
                stompClient.send("/app/typing", {}, JSON.stringify({
                    name: this.selectedUser.email,
                    typing: true
                }));
                const $this = this;
                this.typingTask = setTimeout(function () {
                    stompClient.send("/app/typing", {}, JSON.stringify({
                        name: $this.selectedUser.email,
                        typing: false
                    }))
                    $this.typingTask = null
                }, 500)
            }
        },
        onMessageSend: function () {
            const message = this.message.trim()
            if (message.length > 0) {
                this.message = ''
                const now = new Date()
                const dateString = (now.getHours() > 9 ? now.getHours() : ('0' + now.getHours()))+":"+
                    (now.getMinutes() > 9 ? now.getMinutes() : ('0' + now.getMinutes()))+":"+
                    (now.getSeconds() > 9 ? now.getSeconds() : ('0' + now.getSeconds()));
                this.messages.push({
                    text: message,
                    mine: true,
                    date: dateString
                })
                Vue.set(this.selectedUser, 'desc', message.length > 50 ? message.substr(0, 50) : message)
                Vue.set(this.selectedUser, 'date', dateString)
                this.scrollToLastMessage()
                const messageObj = {message: message, toId: this.selectedUser.id}
                this.$http.post('/api/message/post', messageObj)
                    .then(function (value) {
                    }, function (reason) {
                        console.log(reason)
                    })
            }
        },
        receiveMessage: function(payload) {
            if (this.selectedUser && this.selectedUser.id == payload.fromId) {
                this.messages.push(payload)
                this.scrollToLastMessage()
                Vue.set(this.selectedUser, 'desc', payload.text.length > 50 ? payload.text.substr(0, 50) : payload.text)
                Vue.set(this.selectedUser, 'date', payload.date)
            } else {
                const fromUser = this.findById(payload.fromId)
                Vue.set(fromUser, 'desc', payload.text.length > 50 ? payload.text.substr(0, 50) : payload.text)
                Vue.set(fromUser, 'date', payload.date)
                var count = 1;
                if (fromUser.count) count+=fromUser.count
                Vue.set(fromUser, 'count', count)
            }
        },
        onUserSelected: function (user) {
            user.count = 0
            this.selectedUser = user
            this.loadMessages()
        },
        setTyping: function (payload) {
            Vue.set(this.findById(payload.from), 'typing', payload.typing)
        },
        scrollToLastMessage: function () {
            setTimeout(function () {
                const messageList = document.getElementById('message-container')
                messageList.scrollTop = messageList.scrollHeight
            }, 300)
        },
        findById: function (id) {
            for (var i = 0; i < this.users.online.length; i++) {
                if (this.users.online[i].id == id) {
                    return this.users.online[i]
                }
            }
            for (var i = 0; i < this.users.offline.length; i++) {
                if (this.users.offline[i].id == id) {
                    return this.users.offline[i]
                }
            }
        }
    },
    created: function () {
        this.loadUsers()
    }
});

var socket = new SockJS('/near-me-websocket');
stompClient = Stomp.over(socket);
stompClient.connect({}, function (frame) {
    stompClient.subscribe('/topic/user-list-change', function (payload) {
        app.loadUsers()
    });
    stompClient.subscribe('/user/topic/typing', function (payload) {
        app.setTyping(JSON.parse(payload.body))
    });
    stompClient.subscribe('/user/topic/message', function (payload) {
        app.receiveMessage(JSON.parse(payload.body))
        audio.play()
    });
});