<template>
  <div class="relative flex items-top justify-center min-h-screen bg-gray-100 sm:items-center sm:pt-0">
    <div class="max-w-4xl mx-auto sm:px-6 lg:px-8">
      <el-tabs v-model="activeName" :stretch="true" @tab-click="handleClick">
        <el-tab-pane label="账号登录" name="account-way">
          <div class="mt-8 bg-white overflow-hidden shadow sm:rounded-lg p-6 w-80 h-80">
            <div class="flex justify-center py-16">
              <el-form
                  ref="loginFormRef"
                  :model="loginForm"
                  :label-position="labelPosition"
                  status-icon
                  :rules="rules"
                  label-width="80px">
                <el-form-item label="用户名" prop="userName">
                  <el-input v-model="loginForm.userName"/>
                </el-form-item>
                <el-form-item label="密码" prop="passWord">
                  <el-input v-model="loginForm.passWord" type="password"/>
                </el-form-item>
                <el-form-item>
                  <el-button id="login-btn" type="primary" @click="submitForm(loginFormRef)">登录</el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="扫码登录" name="qrcode-way">
          <div class="mt-8 bg-white overflow-hidden shadow sm:rounded-lg p-6 w-80 h-80">
            <el-skeleton
                class="px-2 py-2"
                :loading="loading"
                animated
                :throttle="500">
              <template #template>
                <el-skeleton-item variant="image" class="h-64"/>
              </template>
              <template #default>
                <div class="px-2 py-2 h-full">
                  <div class="image-qrcode h-full" v-if="!qrcodeInvalid && !confirming">
                    <el-image class="w-full h-full" :src="qrCodeSrc">
                      <template #error>
                        <div class="image-slot">
                          <el-icon>
                            <icon-picture/>
                          </el-icon>
                        </div>
                      </template>
                    </el-image>
                  </div>
                  <div class="qrcode-invalid h-full flex justify-center items-center" v-if="qrcodeInvalid && !confirming">
                    <div>
                      <div class="mb-4">二维码已失效</div>
                      <el-button class="w-full" @click="getQRCode">重新获取</el-button>
                    </div>
                  </div>
                  <div class="qrcode-confirming h-full flex justify-center items-center" v-if="confirming">
                    <div>
                      <div class="mb-4">二维码扫码成功，等待确认</div>
                    </div>
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {reactive, ref} from 'vue'
import type { TabsPaneContext } from 'element-plus'
import {Picture as IconPicture} from '@element-plus/icons-vue'
import axios from 'axios'
import {getUUID} from "~/utils/UUID"
import Socket from '~/utils/websocket'
import { ElMessage } from 'element-plus'
import {useRouter} from "#app"


const router = useRouter()

const activeName = ref('account-way')


const labelPosition = ref('left')

const loginFormRef = ref<FormInstance>()
const loginForm = reactive({
  userName: '',
  passWord: ''
})

const rules = reactive<FormRules>({
  userName: [{required: true, message: "请输入用户名", trigger: 'blur'}],
  passWord: [{required: true, message: "请输入密码", trigger: 'blur'}],
})

const loading = ref(true)

const qrCodeSrc = ref("")

const qrcodeInvalid = ref(false)

const confirming = ref(false)

let qrCode = ref('')

const handleClick = (tab: TabsPaneContext, event: Event) => {
  if (tab.props.name === 'qrcode-way' && loading.value !== false) {
    setTimeout(() => {
      getQRCode();
    }, 2000)
  }
}

const getQRCode = (() => {
  qrCode = ref(getUUID());
  qrcodeInvalid.value = false;
  loading.value = false;
  qrCodeSrc.value = 'http://192.168.0.127:8080/test/api/v1/qrcode/generate/' + qrCode.value
  console.log(qrCode.value);
  const url = "ws://192.168.0.127:8080/test/ws/qrcode/" + qrCode.value;
  const wbSocket = new Socket<T, RT>({ url: url })
  wbSocket.onmessage((data: RT) => {
    console.log('server data:', data)
    console.log('qrCode:', qrCode.value)
    if (data.qrcode === qrCode.value) {
      if (data.status === '1') {
        confirming.value = false;
      } else if (data.status === '2') {
        qrcodeInvalid.value = true
        confirming.value = false
        ElMessage.error('二维码已失效，请重新获取')
        wbSocket.destroy()
      } else if (data.status === '3') {
        confirming.value = true;
        ElMessage.success('二维码扫码成功，等待确认');
      } else if (data.status === '4') {
        ElMessage.success('已确认登录');
        const token = useCookie('token')
        token.value = data.token
        wbSocket.destroy()
        setTimeout(() => {
          router.push({
            // path: '/home',
            name: 'home',
            params: {qrCode: qrCode.value}
          })
        },2000)
      }
    }
  })
})


const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      console.log('submit!')
      axios.post('http://192.168.0.127:8080/test/api/v1/user/login', loginForm).then(res => {
        console.log(res.data);
        if (res.data.code === 0 && res.data.token !== '') {
          ElMessage.success('登录成功');
          const token = useCookie('token')
          token.value = res.data.token
          setTimeout(() => {
            router.push({
              name: 'home',
              params: {token: res.data.token}
            })
          },2000)
        } else {
          ElMessage.error(res.data.data)
        }
      })
    } else {
      console.log('error submit!', fields)
    }
  })
}

</script>

<style scoped>
#login-btn {
  width: 100%;
}

.image-qrcode .image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  font-size: 30px;
}

.image-qrcode .image-slot .el-icon {
  font-size: 30px;
}
</style>