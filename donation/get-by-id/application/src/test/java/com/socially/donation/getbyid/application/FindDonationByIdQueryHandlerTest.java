package com.socially.donation.getbyid.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.getbyid.application.input.FindDonationByIdQuery;
import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.application.output.DonationImageDto;
import com.socially.donation.getbyid.application.output.DonationLocationDto;
import com.socially.donation.getbyid.application.output.DonorSummaryDto;
import com.socially.donation.getbyid.application.output.mapper.DonationDtoMapper;
import com.socially.donation.getbyid.application.output.mapper.DonationImageDtoMapper;
import com.socially.donation.getbyid.domain.port.right.DonationImageMediaUrlPort;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donor.findbyid.application.input.FindDonorByIdQuery;
import com.socially.donor.findbyid.application.port.left.FindDonorByIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDonationByIdQueryHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440010";
  private static final String STORAGE_OBJECT_KEY =
      "donations/" + DONATION_ID + "/images/" + IMAGE_ID + ".jpg";
  private static final StorageObjectKey KEY = StorageObjectKey.from(STORAGE_OBJECT_KEY);
  private static final String MEDIA_URL = "https://cdn.example.com/" + STORAGE_OBJECT_KEY;
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");
  private static final DonationLocationDto LOCATION_DTO =
      new DonationLocationDto("Calle Mayor 1, Madrid", 40.4168, -3.7038);

  private static final DonationImageDto IMAGE_DTO =
      new DonationImageDto(IMAGE_ID, MEDIA_URL, "image/jpeg", 1024L, Boolean.TRUE);

  @Mock private FindDonationByIdRepository donationRepository;

  @Mock private FindDonorByIdUseCase findDonorByIdUseCase;

  @Mock private DonationImageMediaUrlPort donationImageMediaUrlPort;

  @Mock private DonationImageDtoMapper donationImageDtoMapper;

  @Mock private DonationDtoMapper donationDtoMapper;

  @InjectMocks private FindDonationByIdQueryHandler handler;

  private static Donation sampleDonation() {
    return Donation.create(
        Id.from(DONATION_ID),
        Id.from(DONOR_ID),
        Title.from("Test Title"),
        Description.from("Test Description"),
        DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
        CREATED_AT,
        LAST_UPDATED_AT);
  }

  private static DonationImage sampleImage() {
    return DonationImage.create(
        Id.from(IMAGE_ID), KEY, ContentType.from("image/jpeg"), 1024L, Boolean.TRUE, CREATED_AT);
  }

  private static Donation donationWithImage() {
    return sampleDonation().withImageAdded(sampleImage());
  }

  private static Donor sampleDonor() {
    return Donor.create(Id.from(DONOR_ID), Id.from(USER_ID), "a@b.com", "A", "B", CREATED_AT);
  }

  private static DonationDto mappedDonationDto() {
    return new DonationDto(
        Id.from(DONATION_ID),
        Title.from("Test Title"),
        Description.from("Test Description"),
        LOCATION_DTO,
        CREATED_AT,
        LAST_UPDATED_AT,
        new DonorSummaryDto(DONOR_ID, "a@b.com", "A", "B"),
        List.of());
  }

  private void stubDonationFound(Donation donation) {
    when(donationRepository.findById(any(Id.class))).thenReturn(Optional.of(donation));
  }

  private void stubDonorFound(Donor donor) {
    when(findDonorByIdUseCase.execute(new FindDonorByIdQuery(DONOR_ID)))
        .thenReturn(Optional.of(donor));
  }

  private void stubDonationAndDonorFound(Donation donation, Donor donor) {
    stubDonationFound(donation);
    stubDonorFound(donor);
  }

  private void stubEmptyImagesMapping(Donation donation, Donor donor) {
    when(donationDtoMapper.fromDomain(donation, donor, List.of())).thenReturn(mappedDonationDto());
  }

  private void stubImageResolution(Donation donation, Donor donor) {
    when(donationImageMediaUrlPort.mediaUrlFor(KEY)).thenReturn(MEDIA_URL);
    when(donationImageDtoMapper.toDto(sampleImage(), MEDIA_URL)).thenReturn(IMAGE_DTO);
    when(donationDtoMapper.fromDomain(donation, donor, List.of(IMAGE_DTO)))
        .thenReturn(mappedDonationDto());
  }

  @Test
  void execute_should_call_find_by_id() {
    handler.execute(new FindDonationByIdQuery(DONATION_ID));

    verify(donationRepository).findById(Id.from(DONATION_ID));
  }

  @Test
  void execute_should_call_find_donor_by_id_when_donation_found() {
    Donation donation = sampleDonation();
    Donor donor = sampleDonor();
    stubDonationAndDonorFound(donation, donor);
    stubEmptyImagesMapping(donation, donor);

    handler.execute(new FindDonationByIdQuery(DONATION_ID));

    verify(findDonorByIdUseCase).execute(new FindDonorByIdQuery(DONOR_ID));
  }

  @Test
  void execute_should_call_mapper_with_donation_donor_and_empty_images_when_no_images() {
    Donation donation = sampleDonation();
    Donor donor = sampleDonor();
    stubDonationAndDonorFound(donation, donor);
    stubEmptyImagesMapping(donation, donor);

    handler.execute(new FindDonationByIdQuery(DONATION_ID));

    verify(donationDtoMapper).fromDomain(donation, donor, List.of());
  }

  @Test
  void execute_should_return_donation_dto_if_donation_and_donor_found() {
    Donation donation = sampleDonation();
    Donor donor = sampleDonor();
    DonationDto mappedDto = mappedDonationDto();
    stubDonationAndDonorFound(donation, donor);
    when(donationDtoMapper.fromDomain(donation, donor, List.of())).thenReturn(mappedDto);

    DonationDto donationDto = handler.execute(new FindDonationByIdQuery(DONATION_ID)).orElseThrow();

    assertEquals(mappedDto, donationDto);
  }

  @Test
  void execute_should_throw_when_donation_found_but_donor_missing() {
    stubDonationFound(sampleDonation());
    when(findDonorByIdUseCase.execute(new FindDonorByIdQuery(DONOR_ID)))
        .thenReturn(Optional.empty());

    assertThrows(
        IllegalStateException.class, () -> handler.execute(new FindDonationByIdQuery(DONATION_ID)));
    verifyNoInteractions(donationDtoMapper);
    verifyNoInteractions(donationImageMediaUrlPort);
    verifyNoInteractions(donationImageDtoMapper);
  }

  @Test
  void execute_should_return_empty_if_donation_not_found() {
    when(donationRepository.findById(any(Id.class))).thenReturn(Optional.empty());

    Optional<DonationDto> result = handler.execute(new FindDonationByIdQuery(DONATION_ID));

    assertEquals(Optional.empty(), result);
    verifyNoInteractions(findDonorByIdUseCase);
    verifyNoInteractions(donationDtoMapper);
    verifyNoInteractions(donationImageMediaUrlPort);
    verifyNoInteractions(donationImageDtoMapper);
  }

  @Test
  void execute_should_not_call_media_url_port_when_donation_has_no_images() {
    Donation donation = sampleDonation();
    Donor donor = sampleDonor();
    stubDonationAndDonorFound(donation, donor);
    stubEmptyImagesMapping(donation, donor);

    handler.execute(new FindDonationByIdQuery(DONATION_ID));

    verifyNoInteractions(donationImageMediaUrlPort);
  }

  @Test
  void execute_should_not_call_image_dto_mapper_when_donation_has_no_images() {
    Donation donation = sampleDonation();
    Donor donor = sampleDonor();
    stubDonationAndDonorFound(donation, donor);
    stubEmptyImagesMapping(donation, donor);

    handler.execute(new FindDonationByIdQuery(DONATION_ID));

    verifyNoInteractions(donationImageDtoMapper);
  }

  @Test
  void execute_should_call_media_url_port_per_image() {
    Donation donation = donationWithImage();
    Donor donor = sampleDonor();
    stubDonationAndDonorFound(donation, donor);
    when(donationImageMediaUrlPort.mediaUrlFor(KEY)).thenReturn(MEDIA_URL);
    when(donationDtoMapper.fromDomain(eq(donation), eq(donor), any()))
        .thenReturn(mappedDonationDto());

    handler.execute(new FindDonationByIdQuery(DONATION_ID));

    verify(donationImageMediaUrlPort).mediaUrlFor(KEY);
  }

  @Test
  void execute_should_call_image_dto_mapper_with_image_and_resolved_url() {
    Donation donation = donationWithImage();
    Donor donor = sampleDonor();
    DonationImage image = sampleImage();
    stubDonationAndDonorFound(donation, donor);
    when(donationImageMediaUrlPort.mediaUrlFor(KEY)).thenReturn(MEDIA_URL);
    when(donationDtoMapper.fromDomain(eq(donation), eq(donor), any()))
        .thenReturn(mappedDonationDto());

    handler.execute(new FindDonationByIdQuery(DONATION_ID));

    verify(donationImageDtoMapper).toDto(image, MEDIA_URL);
  }

  @Test
  void execute_should_pass_built_image_dtos_to_donation_dto_mapper() {
    Donation donation = donationWithImage();
    Donor donor = sampleDonor();
    stubDonationAndDonorFound(donation, donor);
    stubImageResolution(donation, donor);

    handler.execute(new FindDonationByIdQuery(DONATION_ID));

    verify(donationDtoMapper).fromDomain(donation, donor, List.of(IMAGE_DTO));
  }
}
