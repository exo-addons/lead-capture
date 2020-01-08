<template>
  <ckeditor
    v-model="editorData"
    :editor="editor"
    :config="editorConfig" />
</template>

<script>
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';
export default {
  props: {
    content: {
      type: String,
    },
  },
  data() {
    return {
      editor: ClassicEditor,
      editorData: this.content,
      editorConfig: {
        alignment: {
          options: ['left', 'right'],
        },
      },
    };
  },

  methods: {
    getContent() {
      return this.editorData;
    },
        setContent(content) {
      this.editorData=content;
    },
  },
};
</script>