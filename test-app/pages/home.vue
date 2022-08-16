<template>
  <div class="relative flex items-top justify-center min-h-screen bg-gray-100 sm:items-center sm:pt-0">
    <div class="max-w-4xl mx-auto sm:px-6 lg:px-8">
      <div class="mt-8 bg-white overflow-hidden shadow sm:rounded-lg p-6">
        <h2 class="text-2xl leading-7 font-semibold">
          Welcome,
          <a href="#" class="button--doc text-green-500 hover:underline">
            {{ userName }}
          </a>
        </h2>
        <div class="mt-4 pt-4 text-pink-500 border-t border-dashed break-all">
          {{ state }}
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>

import {ref} from "vue";
import {useRouter} from "#app";
import axios from "axios";
import {ElMessage} from "element-plus";

let userName = ref('')

const router = useRouter()
const route = useRoute()

const qrCode = route.params.qrCode
const token = route.params.token

const state = useCookie('token')
if (state.value === undefined || state.value === '') {
  router.push({
    path: '/login'
  })
}

axios.post('http://192.168.0.127:8080/test/api/v1/user/profile',{}, {headers: {token: state.value}}).then(res => {
  console.log(res.data)
  if (res.data.code === 0) {
    userName.value = res.data.data.userName
  } else {
    ElMessage.error(res.data.msg)
  }
}).catch(reason => {
  ElMessage.error(reason)
})

</script>

<style scoped>

</style>