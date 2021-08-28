<template>

<v-data-table
    :headers="headers"
    :items="myPage"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'MyPage',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "accountNo", value: "accountNo" },
            { text: "lastModifiedDate", value: "lastModifiedDate" },
            { text: "lastRequestId", value: "lastRequestId" },
            { text: "lastRequestName", value: "lastRequestName" },
            { text: "requestMoney", value: "requestMoney" },
            { text: "amountOfMoney", value: "amountOfMoney" },
            { text: "requestStatus", value: "requestStatus" },
        ],
        myPage : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/mypages')

      this.myPage = temp.data._embedded.mypages;

    },
    methods: {
    }
  }
</script>

