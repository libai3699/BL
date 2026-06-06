<script setup>
import { onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils/navigate';

const commonStore = useCommonStore();

// 页面初始化回调
const onPageInit = async () => {};

onShow(() => {
  // 返回方法
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="m-content">
      <nav-bar
        :title="`${$t('account.register.protocol')} & ${$t('account.register.privacy')}`"
      />

      <div class="content">
        <p style="margin-top: 20rpx"></p>
        <h1 class="title">Aviso del registro de la cuenta institucional de acciones</h1>
        <p></p>
        <h4>1.Naturaleza de la cuenta</h4>
        <p>
          Esta es una cuenta institucional de acciones destinada principalmente a
          participar en inversiones del mercado primario de acciones.
        </p>
        <p>
          La cuenta es proporcionada por BCB, bajo la supervisión de la Comisión de Bolsa
          y Valores de EE. UU. (SEC), y los documentos regulatorios correspondientes se
          están tramitando ante la Comisión Nacional Bancaria y de Valores (CNBV) de
          México.
        </p>
        <p>
          La cuenta es de uso personal exclusivo y solo puede ser operada por el titular
          vinculada a una cuenta bancaria personal, garantizando la seguridad y el
          cumplimiento normativo.
        </p>

        <h4>2.Requisitos para la solicitud</h4>
        <p>• Tener al menos 18 años y contar con una identificación legal válida</p>
        <p>• Tener conciencia básica sobre los riesgos de inversión</p>
        <p>
          • Proporcionar todos los documentos requeridos para la apertura de la cuenta
        </p>
        <p>
          • Contar con la capacidad financiera adecuada y aceptar las reglas del modelo de
          inversión institucional
        </p>

        <h4>3.Documentación requerida</h4>
        <p>
          Por favor, prepare los siguientes documentos para el proceso de verificación:
        </p>
        <p>• Identificación oficial válida</p>
        <p>• Comprobante de domicilio</p>
        <p>• Información bancaria válida (para depósitos y retiros)</p>
        <p>• Formulario de registro de cuenta institucional debidamente completado</p>

        <h4>4.Proceso de apertura de la cuenta</h4>
        <p>• Envío del formulario de registro y la documentación requerida</p>
        <p>
          • Revisión y validación de los documentos (aproximadamente 1-2 días hábiles)
        </p>
        <p>
          • Una vez aprobada, se le asistirá para completar el registro y activación de la
          cuenta de trading
        </p>
        <p>
          • Vinculación de su cuenta bancaria personal y finalización de la certificación
          de la cuenta
        </p>
        <p>
          • Descarga de la plataforma oficial de trading, activación completa de la cuenta
          e inicio de operaciones
        </p>

        <h4>5.Reglas para depósitos y retiros</h4>
        <p>
          • Todos los depósitos y retiros se gestionan a través de la cuenta institucional
          con supervisión regulatoria completa, garantizando transparencia y cumplimiento.
        </p>
        <p>
          • Los retiros siguen el mecanismo T+1 del mercado bursátil: La solicitud se
          presenta el mismo día y los fondos se acreditan en su cuenta bancaria vinculada
          al día siguiente.
        </p>
        <p>
          • Los registros de depósitos y retiros pueden consultarse en todo momento dentro
          de la cuenta con recibos electrónicos disponibles para descarga.
        </p>

        <h4>6.Instrucciones de operación</h4>
        <p>
          • Todas las operaciones de trading deben ser confirmadas y ejecutadas
          personalmente por usted.
        </p>

        <h4>7.Aviso de riesgos</h4>
        <p>
          • La inversión en acciones conlleva ciertos riesgos, por lo que debe participar
          de acuerdo con su situación financiera personal.
        </p>
        <p>
          • Aunque la estrategia de inversión institucional se enfoca principalmente en el
          mercado primario con bajo riesgo siempre existen riesgos asociados a la
          volatilidad del mercado.
        </p>
      </div>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;
.m-content {
  @include hasNavBar();

  h1,
  h4 {
    color: #333;
    font-family:
      'PingFang SC', '微软雅黑', 'YaHei', '黑体', 'Hei', Tahoma, Helvetica, arial,
      sans-serif;
  }

  p {
    font-size: 18px;
    line-height: 1.6;
    color: #999;
  }

  .title {
    font-size: 26px;
    font-weight: 600;
    position: relative;
    text-align: center;
    color: #000;
  }

  .content {
    padding: 12px;
  }

  .content p {
    font-size: 18px;
    font-weight: normal;
    margin: 10px 0;
    color: #232323;
  }

  .content h4 {
    font-size: 22px;
    font-weight: 600;
    color: #000;
    margin-top: 40rpx;
  }
}
</style>
