<template>
<v-container class="">
    <v-list-item>
        <v-list-item-title>
            <v-btn fab dark small color="blue darken-2" @click="addNote()">
                <v-icon>mdi-plus</v-icon>
            </v-btn>
            Comments
        </v-list-item-title>
        <a fab dark small style="float: right;" :href="lead.taskUrl" target="_blank">
            <v-icon>mdi-open-in-new</v-icon>
        </a>

    </v-list-item>
    <v-list elevation="2">
        <v-list-item :key="item.noteMessage" v-for="item in comments">
            <v-list-item-content>
                <v-list-item-title>{{ item.comment }}</v-list-item-title>
                <v-list-item-subtitle>{{ item.createdTime }}</v-list-item-subtitle>
                <v-divider></v-divider>
            </v-list-item-content>
        </v-list-item>
    </v-list>

    <v-dialog max-width="600px" persistent v-model="dialog">
        <v-card>
            <v-card-title>
                <span class="headline">New note</span>
            </v-card-title>
            <v-card-text>
                <v-container>
                    <v-row>
                        <v-col>
                            <v-textarea label="Solo textarea" name="input-7-4" solo v-model="newNote.comment"></v-textarea>
                        </v-col>
                    </v-row>
                </v-container>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn @click="close()" color="blue darken-1" text>Close</v-btn>
                <v-btn @click="saveNote()" color="blue darken-1" text>Save</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</v-container>
</template>

<script>
export default {
    props: ['lead', 'comments'],
    data: () => ({
        dialog: false,
        newNote: {}
    }),
    methods: {
        addNote() {
            this.dialog = true
        },
        close() {
            this.newNote = {}
            this.dialog = false
        },
        saveNote() {
            this.newNote.createdTime = "15/12/2010";
            this.comments.push(this.newNote)
            this.newNote = {}
            this.dialog = false
        }
    }
}
</script>
