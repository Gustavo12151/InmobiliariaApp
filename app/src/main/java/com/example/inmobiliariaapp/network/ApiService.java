package com.example.inmobiliariaapp.network;

import com.example.inmobiliariaapp.models.Propietario;
import com.example.inmobiliariaapp.models.Inmueble;
import com.example.inmobiliariaapp.models.Contrato;
import com.example.inmobiliariaapp.models.Pago;

import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // ====================================================
    // 🔐 LOGIN
    // ====================================================
    @FormUrlEncoded
    @POST("api/Propietarios/login")
    Call<ResponseBody> login(
            @Field("Usuario") String usuario,
            @Field("Clave") String clave
    );

    // ====================================================
    // 👤 PROPIETARIO
    // ====================================================

    @GET("api/Propietarios")
    Call<Propietario> getPerfil(@Header("Authorization") String token);

    @PUT("api/Propietarios/actualizar")
    Call<Propietario> actualizarPerfil(
            @Header("Authorization") String token,
            @Body Propietario propietario
    );

    // Resetear clave (genera clave nueva)
    @FormUrlEncoded
    @POST("api/Propietarios/email")
    Call<ResponseBody> resetearClave(
            @Field("email") String email
    );

    // Cambiar clave
    @FormUrlEncoded
    @PUT("api/Propietarios/changePassword")
    Call<Void> cambiarClave(
            @Header("Authorization") String token,
            @Field("currentPassword") String actual,
            @Field("newPassword") String nueva
    );


    // ====================================================
    // 🏠 INMUEBLES
    // ====================================================
    @GET("api/Inmuebles")
    Call<List<Inmueble>> obtenerInmuebles(@Header("Authorization") String token);

    @GET("api/Inmuebles/GetContratoVigente")
    Call<List<Inmueble>> getInmueblesConContrato(@Header("Authorization") String token);

    @Multipart
    @POST("api/Inmuebles/cargar")
    Call<Inmueble> agregarInmueble(
            @Header("Authorization") String token,
            @Part MultipartBody.Part imagen,
            @Part("inmueble") RequestBody inmuebleJson
    );

    @PUT("api/Inmuebles/actualizar")
    Call<Inmueble> actualizarInmueble(
            @Header("Authorization") String token,
            @Body Inmueble inmueble
    );

    // ====================================================
    // 📄 CONTRATOS
    // ====================================================
    @GET("api/Contratos/inmueble/{id}")
    Call<Contrato> getContratoPorInmueble(
            @Header("Authorization") String token,
            @Path("id") int idInmueble
    );

    // ====================================================
    // 💸 PAGOS
    // ====================================================
    @GET("api/Pagos/contrato/{id}")
    Call<List<Pago>> getPagosPorContrato(
            @Header("Authorization") String token,
            @Path("id") int idContrato
    );
}
