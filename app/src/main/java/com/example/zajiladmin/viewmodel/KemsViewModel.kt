import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.zajiladmin.models.VerifyEmailDataModel
import com.example.zajiladmin.repository.KemsRepository
import com.example.zajiladmin.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class KemsViewModel(
    app: Application,
    private val kemsRepository: KemsRepository
) : AndroidViewModel(app) {



    private fun handleVerifySendResponse(response: Response<VerifyEmailDataModel?>): Resource<VerifyEmailDataModel?> {


        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    val verifyEmailLiveData: MutableLiveData<Resource<VerifyEmailDataModel?>> =
        MutableLiveData()


    fun getVerifyModel(email: String) {
        verifyEmailLiveData.postValue(Resource.Loading())

        viewModelScope.launch {
            try {
                var response = handleVerifySendResponse(
                    kemsRepository.getVerifyEmail(email)
                )

                when (response) {
                    is Resource.Success -> {
                        var verifyEmailResponse: VerifyEmailDataModel? = response.data
                        if (verifyEmailResponse != null) {
                            Log.d("TAG", "GetTadeelIlawaIjtama: $verifyEmailResponse");
                            verifyEmailLiveData.postValue(Resource.Success(verifyEmailResponse))

                        }

                    }
                    is Resource.Error -> {
                        verifyEmailLiveData.postValue(Resource.Error(response.message ?: "Error from api"))
                    }
                    is Resource.Loading -> {

                    }
                }
            }catch (e: Exception){
                verifyEmailLiveData.postValue(Resource.Error(e.message ?: "Exception from verufy email code"))



            }

        }
    }



}