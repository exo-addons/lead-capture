<template>
<v-container class="">
    <v-list-item>
        <v-list-item-title>
            Comments
        </v-list-item-title>

        <v-btn class="ma-2" outlined @click="addNote()">
            <v-icon>mdi-plus</v-icon>
            Add Comment
        </v-btn>
        <a fab dark small style="float: right;" :href="lead.taskUrl" target="_blank">
            <v-icon>mdi-open-in-new</v-icon>
        </a>

        <v-list-item-action>
                            <i class="uiCloseIcon" @click="toggleDrawer()"></i>
                        </v-list-item-action>
    </v-list-item>
    <v-card v-show="dialog">
        <v-card-text>
            <ck-editor ref="ck" :content="content" />
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn @click="close()" color="blue darken-1" text>Close</v-btn>
            <v-btn @click="saveNote()" color="blue darken-1" text>Save</v-btn>
        </v-card-actions>
    </v-card>
    <v-list>
        <v-list-item :key="item.comment" v-for="item in comments">
            <div class="commentItem ">

                <div class="commmentLeft pull-left">
                    <a class="avatarXSmall" :href="`/portal/intranet/profile/${item.author}`">
                        <img :src="`/rest/v1/social/users/${item.author}/avatar`" :alt="item.authorName">
                    </a>
                </div>
                <div class="commentRight">
                    <div class="author">
                        <a :href="`/portal/intranet/profile/${item.author}`">{{item.authorName}}</a>
                        <span class="date"> {{item.createdDate}} </span>
                    </div>
                    <span class="contentComment clearfix">
                        <div v-html="item.comment"></div>
                    </span>
                </div>

            </div>

        </v-list-item>
    </v-list>
</v-container>
</template>

<script>
import ckEditor from '../commons/ckEditor.vue';

export default {
    components: {
        ckEditor
    },
    props: ['lead', 'comments'],
    data: () => ({
        dialog: false,
        content: ""
    }),
    methods: {
        toggleDrawer() {
            this.$emit('toggleDrawer');
        },
        addNote() {
            this.dialog = true
        },
        close() {
            this.newNote = {}
            this.dialog = false
        },
        saveNote() {
            if (this.$refs.ck !== undefined) {

                fetch(`/portal/rest/leadcapture/leadsmanagement/comments/` + this.lead.taskId, {
                        method: 'post',
                        credentials: 'include',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: this.$refs.ck.getContent(),
                    })
                    .then((resp) => resp.json())
                    .then((resp) => {
                        this.comments.unshift(resp);
                    });
                this.$refs.ck.setContent("")
                this.dialog = false
            }

        }
    }
}
</script>

<style>
.commentItem {
    width: 100%;
}

.avatarXSmall {
    max-height: 36px;
    max-width: 36px;
}

.commmentLeft+.commentRight {
    margin-left: 50px;
}

.date {
    margin-left: 10px;
    font-size: 12px;
    padding-right: 16px;
    float: right;
}

.comment {
    width: 100%;
}
</style>
