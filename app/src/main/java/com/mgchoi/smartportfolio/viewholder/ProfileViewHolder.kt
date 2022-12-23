package com.mgchoi.smartportfolio.viewholder

import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.model.ViewStyle
import com.mgchoi.smartportfolio.databinding.RowProfileBinding
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.tool.ProfileImageHelper

class ProfileViewHolder(val context: Context, val binding: RowProfileBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        const val VIEW_TYPE = R.layout.row_profile
    }

    var onNameEditRequest: (() -> Unit)? = null
    var onProfileImageEditRequest: (() -> Unit)? = null
    var onViewStyleChange: ((ViewStyle) -> Unit)? = null

    fun bind(member: Member) {
        // 기존 이름 설정
        binding.txtRowProfileName.text = member.name

        // 기존 뷰 스타일 데이터 설정
        binding.spinnerRowProfileViewStyle.setSelection(member.viewStyle.rawValue)
        // 뷰 스타일 Spinner의 OnItemSelectedListener 설정
        binding.spinnerRowProfileViewStyle.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, position: Int, i: Long) {
                    // ViewStyle의 rawValue는 Spinner의 position에 대응
                    member.viewStyle = ViewStyle.of(position)
                    // ViewStyle이 변경된 것을 알림
                    onViewStyleChange?.let { it(ViewStyle.of(position)) }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }

        // 프로필 이미지 관리
        val manager = ProfileImageHelper(context)
        // 선택된 이미지가 있으면 해당 이미지를 설정하고, 없으면 이름의 해시값에 대응된 기본 이미지 출력
        val profileImage = member.image?.let { manager.read(it) }
            ?: manager.defaultProfileImage(member.name)
        binding.imgProfile.setImageBitmap(profileImage)

        // 이미지 수정 클릭
        binding.frameProfilePic.setOnClickListener {
            onProfileImageEditRequest?.let { it() }
        }

        // 이름 수정 클릭
        binding.layoutRowProfileEditName.setOnClickListener {
            onNameEditRequest?.let { it() }
        }


    }
}